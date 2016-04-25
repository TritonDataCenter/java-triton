package com.joyent.triton;

import com.joyent.triton.config.ChainedConfigContext;
import com.joyent.triton.config.ConfigContext;
import com.joyent.triton.config.DefaultsConfigContext;
import com.joyent.triton.config.SystemSettingsConfigContext;
import com.joyent.triton.domain.Instance;
import com.joyent.triton.domain.Package;
import com.joyent.triton.http.CloudApiConnectionContext;
import com.joyent.triton.queryfilters.InstanceFilter;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.joyent.triton.queryfilters.PackageFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.threeten.bp.Duration;
import org.threeten.bp.temporal.ChronoUnit;

import java.io.IOException;
import java.util.*;

import static org.testng.Assert.*;

@Test(groups = { "integration" })
public class InstancesIT {
    private static final String TEST_TAG_KEY = "server_type";
    private static final String TEST_TAG = "java-integration-test";
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private ConfigContext config = null;
    private Instances instanceApi = null;
    private CloudApi cloudApi = null;
    private UUID packageId = null;

    // US-EAST
    private UUID imageId = UUID.fromString("e1faace4-e19b-11e5-928b-83849e2fd94a");

    // US-EAST-3B
//    private UUID imageId = UUID.fromString("e1faace4-e19b-11e5-928b-83849e2fd94a");

    @BeforeClass
    public void setup() throws IOException {
        this.config = new ChainedConfigContext(
                new DefaultsConfigContext(),
                new SystemSettingsConfigContext()
        );
        this.cloudApi = new CloudApi(config);
        this.instanceApi = cloudApi.instances();
        this.packageId = findIntegrationTestPackage().getId();
    }

    @AfterClass
    public void cleanUp() throws IOException {
        logger.info("Cleaning up any lingering instances");
        try (CloudApiConnectionContext context = cloudApi.createConnectionContext()) {
            final InstanceFilter filter = new InstanceFilter()
                    .setTags(Collections.singletonMap(TEST_TAG_KEY, TEST_TAG));
            final Iterator<Instance> instances = instanceApi.list(context, filter);

            while (instances.hasNext()) {
                final Instance instance = instances.next();

                if (instance.getState().equals("running")) {
                    instanceApi.delete(context, instance);
                }
            }
        }
    }

    private Package findIntegrationTestPackage() throws IOException {
        final Collection<Package> smallPackages = cloudApi.packages().smallestMemory();

        if (smallPackages.isEmpty()) {
            throw new IllegalArgumentException("There are no valid packages defined");
        }

        final Package smallest = smallPackages.iterator().next();
        logger.debug("Package used for integration tests: {}", smallest);

        return smallest;
    }

    @Test
    public void canListNoInstances() throws IOException {

        try (CloudApiConnectionContext context = cloudApi.createConnectionContext()) {
            final Iterator<Instance> instances = instanceApi.list(context);

            try {
                assertFalse(instances.hasNext(), "You must run tests on a DC with no instances");
            } catch (AssertionError e) {
                final Set<Instance> results = Sets.newHashSet(instances);
                logger.error("Total number of instances: {}", results.size());

                if (results.size() == 1) {
                    logger.error("Single instance: {}", results.iterator().next());
                }

                throw e;
            }
        }
    }

    /**
     * Integration test that checks to see if we can create an instance, wait for instances
     * to change state, list newly created instances and delete the newly created instances.
     */
    @Test(dependsOnMethods = "canListNoInstances")
    public void canCreateWaitListAndDeleteMachines() throws IOException {
        try (CloudApiConnectionContext context = cloudApi.createConnectionContext()) {
            Map<String, String> tags = Collections.singletonMap(TEST_TAG_KEY, TEST_TAG);

            // CREATE
            Instance instance1 = new Instance()
                    .setName("unit-test-machine-" + UUID.randomUUID())
                    .setPackageId(packageId)
                    .setImage(imageId)
                    .setTags(tags);

            Instance instance2 = new Instance()
                    .setName("unit-test-machine-" + UUID.randomUUID())
                    .setPackageId(packageId)
                    .setImage(imageId)
                    .setTags(tags);

            Instance created1 = instanceApi.create(context, instance1);
            Instance created2 = instanceApi.create(context, instance2);

            // WAIT

            Duration waitTime = Duration.of(3, ChronoUnit.MINUTES);
            Duration waitInterval = Duration.of(10, ChronoUnit.SECONDS);

            final Instance running1 = instanceApi.waitForStateChange(context,
                    created1, "provisioning", waitTime.toMillis(),
                    waitInterval.toMillis());
            final Instance running2 = instanceApi.waitForStateChange(context,
                    created2, "provisioning", waitTime.toMillis(),
                    waitInterval.toMillis());

            try {
                assertEquals(running1.getState(), "running");
            } catch (AssertionError e) {
                logger.error("Instance was not running as expected: {}", running1);
                throw e;
            }

            try {
                assertEquals(running2.getState(), "running");
            } catch (AssertionError e) {
                logger.error("Instance was not running as expected: {}", running2);
                throw e;
            }


            // LIST
            Iterator<Instance> instancesItr = instanceApi.list(context);
            assertTrue(instancesItr.hasNext(), "No instances were created");

            Set<Instance> instances = Sets.newHashSet(instancesItr);
            Set<UUID> ids = FluentIterable.from(instances).transform(
                    new Function<Instance, UUID>() {
                        @Override
                        public UUID apply(Instance instance) {
                            return instance.getId();
                        }
                    }).toSet();

            assertTrue(ids.contains(running1.getId()),
                    String.format("No instance with the id [%s] found", running1.getId()));
            assertTrue(ids.contains(running2.getId()),
                    String.format("No instance with the id [%s] found", running2.getId()));

            // DELETE

            instanceApi.delete(context, running1);
            instanceApi.delete(context, running2);

            // WAIT

            instanceApi.waitForStateChange(context,
                    created1, "running", waitTime.toMillis(), waitInterval.toMillis());
            instanceApi.waitForStateChange(context,
                    created2, "running", waitTime.toMillis(), waitInterval.toMillis());
        }
    }

    @Test(dependsOnMethods = "canCreateWaitListAndDeleteMachines" )
    public void addTagsToInstance() throws IOException {
        try (CloudApiConnectionContext context = cloudApi.createConnectionContext()) {
            Map<String, String> firstTags = ImmutableMap.of(
                TEST_TAG_KEY, TEST_TAG,
                "existing", "existing-value1"
            );

            // CREATE
            Instance instance = new Instance()
                    .setName("unit-test-machine-" + UUID.randomUUID())
                    .setPackageId(packageId)
                    .setImage(imageId)
                    .setTags(firstTags);

            Instance created = instanceApi.create(context, instance);

            Map<String, String> newTags = ImmutableMap.of(
                    "new-tag1", "new-value1",
                    "new-tag2", "new-value2",
                    "existing", "existing-value2"
            );

            // WAIT

            Duration waitTime = Duration.of(3, ChronoUnit.MINUTES);
            Duration waitInterval = Duration.of(10, ChronoUnit.SECONDS);

            final Instance running = instanceApi.waitForStateChange(context,
                    created, "provisioning", waitTime.toMillis(),
                    waitInterval.toMillis());

            // UPDATE TAGS
            instanceApi.addTags(context, created.getId(), newTags);

            // Add tags is async, so we wait for it
            try {
                Thread.sleep(20_000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            final Instance updated = instanceApi.findById(context, running.getId());
            final Map<String, String> tags = updated.getTags();

            assertEquals(tags.get(TEST_TAG_KEY), TEST_TAG);
            assertEquals(tags.get("existing"), "existing-value2");
            assertEquals(tags.get("new-tag1"), "new-value1");
            assertEquals(tags.get("new-tag2"), "new-value2");

            // DELETE

            instanceApi.delete(context, running.getId());

            // WAIT
            instanceApi.waitForStateChange(context,
                    running, "running", waitTime.toMillis(),
                    waitInterval.toMillis());
        }
    }

    @Test(dependsOnMethods = "addTagsToInstance")
    public void filterInstanceList() throws IOException {
        final String searchKey = "search_key";
        final String searchVal = "search_value";
        try (CloudApiConnectionContext context = cloudApi.createConnectionContext()) {
            Map<String, String> searchTags = ImmutableMap.of(
                    searchKey, searchVal,
                    TEST_TAG_KEY, TEST_TAG
            );

            Map<String, String> notFoundTags = ImmutableMap.of(
                    "not_found_key", "not_found",
                    TEST_TAG_KEY, TEST_TAG
            );

            // CREATE
            final Instance instance1 = instanceApi.create(context, new Instance()
                    .setName("unit-test-machine-" + UUID.randomUUID())
                    .setPackageId(packageId)
                    .setImage(imageId)
                    .setTags(searchTags));

            final Instance instance2 = instanceApi.create(context, new Instance()
                    .setName("unit-test-machine-" + UUID.randomUUID())
                    .setPackageId(packageId)
                    .setImage(imageId)
                    .setTags(notFoundTags));

            final Instance instance3 = instanceApi.create(context, new Instance()
                    .setName("unit-test-machine-" + UUID.randomUUID())
                    .setPackageId(packageId)
                    .setImage(imageId)
                    .setTags(searchTags));

            Duration waitTime = Duration.of(3, ChronoUnit.MINUTES);
            Duration waitInterval = Duration.of(10, ChronoUnit.SECONDS);

            instanceApi.waitForStateChange(context,
                    instance1, "provisioning", waitTime.toMillis(),
                    waitInterval.toMillis());

            instanceApi.waitForStateChange(context,
                    instance2, "provisioning", waitTime.toMillis(),
                    waitInterval.toMillis());

            instanceApi.waitForStateChange(context,
                    instance3, "provisioning", waitTime.toMillis(),
                    waitInterval.toMillis());

            InstanceFilter filter = new InstanceFilter()
                    .setTags(ImmutableMap.of(searchKey, searchVal));
            Iterator<Instance> instancesItr = instanceApi.list(context, filter);

            assertTrue(instancesItr.hasNext(), "No instances were found by filter");
            List<Instance> instanceList = Lists.newArrayList(instancesItr);

            final Set<UUID> expectedIds = ImmutableSet.of(
                    instance1.getId(), instance3.getId());

            assertEquals(instanceList.size(), 2);
            final boolean matched = FluentIterable.from(instanceList)
                    .allMatch(new Predicate<Instance>() {
                @Override
                public boolean apply(Instance instance) {
                    return expectedIds.contains(instance.getId());
                }
            });

            assertTrue(matched, "Unable to filter on tags");

            instanceApi.delete(instance1);
            instanceApi.delete(instance2);
            instanceApi.delete(instance3);

            instanceApi.waitForStateChange(context,
                    instance1.getId(), "running", waitTime.toMillis(),
                    waitInterval.toMillis());

            instanceApi.waitForStateChange(context,
                    instance2.getId(), "running", waitTime.toMillis(),
                    waitInterval.toMillis());

            instanceApi.waitForStateChange(context,
                    instance3.getId(), "running", waitTime.toMillis(),
                    waitInterval.toMillis());
        }
    }
}
