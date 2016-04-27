package com.joyent.triton;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.joyent.triton.config.ChainedConfigContext;
import com.joyent.triton.config.ConfigContext;
import com.joyent.triton.config.DefaultsConfigContext;
import com.joyent.triton.config.StandardConfigContext;
import com.joyent.triton.domain.Instance;
import com.joyent.triton.exceptions.CloudApiIOException;
import com.joyent.triton.exceptions.CloudApiResponseException;
import com.joyent.triton.http.CloudApiConnectionContext;
import com.joyent.triton.http.CloudApiHttpHeaders;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NoHttpResponseException;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.FileEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.protocol.HttpContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;

import static com.joyent.triton.FakeHttpClient.createMockContext;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

@Test(groups = { "unit" })
public class InstancesTest {
    private static final String TEST_TAG_KEY = "server_type";
    private static final String TEST_TAG = "java-unit-test";
    private static final ProtocolVersion HTTP_1_1 = new HttpVersion(1, 1);

    private ConfigContext config = null;
    private Instances instanceApi = null;
    private CloudApi cloudApi = null;

    @BeforeClass
    public void setup() {
        this.config = new ChainedConfigContext(
                new DefaultsConfigContext(),
                new StandardConfigContext()
                        .setNoAuth(true)

        );
        this.cloudApi = new CloudApi(this.config);
        this.instanceApi = cloudApi.instances();
    }

    @Test(expectedExceptions = CloudApiIOException.class)
    public void canHandleNoResponseException() throws IOException {
        final CloudApiConnectionContext mockContext = mock(CloudApiConnectionContext.class);
        when(mockContext.getHttpContext()).thenReturn(new HttpClientContext());
        final HttpClient mockClient = mock(HttpClient.class);
        when(mockContext.getHttpClient()).thenReturn(mockClient);

        when(mockClient.execute(
                isA(HttpUriRequest.class),
                (ResponseHandler<?>)isA(ResponseHandler.class),
                isA(HttpContext.class)
        )).thenThrow(new NoHttpResponseException("No Response Simulation"));

        instanceApi.findById(mockContext, new UUID(0L, 0L));
    }

    public void canCreateInstance() throws IOException {
        final StatusLine statusLine = new BasicStatusLine(HTTP_1_1, HttpStatus.SC_CREATED, "Created");
        final HttpResponse response = new BasicHttpResponse(statusLine);
        final File file = new File("src/test/data/instances/created.json");
        final HttpEntity entity = new FileEntity(file);
        response.setEntity(entity);

        Instance instance = new Instance()
                .setName("some_name")
                .setPackageId(new UUID(12L, 24L))
                .setImage(new UUID(8L, 16L))
                .setTags(Collections.singletonMap(TEST_TAG_KEY, TEST_TAG));

        try (CloudApiConnectionContext context = createMockContext(response)) {
            final Instance created = instanceApi.create(context, instance);
            assertNotNull(created);
            assertNotNull(created.getId());
        }
    }

    public void wontCreateInstanceWithoutPackage() throws IOException {
        final StatusLine statusLine = new BasicStatusLine(HTTP_1_1, HttpStatus.SC_CREATED, "Created");
        final HttpResponse response = new BasicHttpResponse(statusLine);
        final File file = new File("src/test/data/instances/created.json");
        final HttpEntity entity = new FileEntity(file);
        response.setEntity(entity);

        Instance instance = new Instance()
                .setName("some_name")
                .setImage(new UUID(8L, 16L))
                .setTags(Collections.singletonMap(TEST_TAG_KEY, TEST_TAG));

        boolean thrown = false;

        try (CloudApiConnectionContext context = createMockContext(response)) {
            final Instance created = instanceApi.create(context, instance);
            assertNotNull(created);
        } catch (NullPointerException e) {
            if (e.getMessage().equals("Package id must be present")) {
                thrown = true;
            } else {
                fail("Unexpected NPE message: " + e.getMessage());
            }
        }

        assertTrue(thrown, "Expected exception was never thrown");
    }

    public void wontCreateInstanceWithoutImage() throws IOException {
        final StatusLine statusLine = new BasicStatusLine(HTTP_1_1, HttpStatus.SC_CREATED, "Created");
        final HttpResponse response = new BasicHttpResponse(statusLine);
        final File file = new File("src/test/data/instances/created.json");
        final HttpEntity entity = new FileEntity(file);
        response.setEntity(entity);

        Instance instance = new Instance()
                .setName("some_name")
                .setPackageId(new UUID(12L, 24L))
                .setTags(Collections.singletonMap(TEST_TAG_KEY, TEST_TAG));

        boolean thrown = false;

        try (CloudApiConnectionContext context = createMockContext(response)) {
            final Instance created = instanceApi.create(context, instance);
            assertNull(created);
        } catch (NullPointerException e) {
            if (e.getMessage().equals("Image id must be present")) {
                thrown = true;
            } else {
                fail("Unexpected NPE message: " + e.getMessage());
            }
        }

        assertTrue(thrown, "Expected exception was never thrown");
    }

    public void canListWithNoInstances() throws IOException {
        final StatusLine statusLine = new BasicStatusLine(HTTP_1_1, HttpStatus.SC_OK, "OK");
        final HttpResponse response = new BasicHttpResponse(statusLine);
        response.setHeader(CloudApiHttpHeaders.X_RESOURCE_COUNT, "0");
        response.setHeader(CloudApiHttpHeaders.X_QUERY_LIMIT, "1000");

        try (CloudApiConnectionContext context = createMockContext(response)) {
            Iterator<Instance> itr = instanceApi.list(context);
            assertFalse(itr.hasNext(), "This should be an empty iterator");
        }
    }

    public void canListWithInstancesUnderQueryLimit() throws IOException {
        final StatusLine statusLine = new BasicStatusLine(HTTP_1_1, HttpStatus.SC_OK, "OK");
        final HttpResponse response1 = new BasicHttpResponse(statusLine);
        response1.setHeader(CloudApiHttpHeaders.X_RESOURCE_COUNT, "2");
        response1.setHeader(CloudApiHttpHeaders.X_QUERY_LIMIT, "1000");

        final HttpResponse response2 = new BasicHttpResponse(statusLine);
        response2.setHeader(CloudApiHttpHeaders.X_RESOURCE_COUNT, "2");
        response2.setHeader(CloudApiHttpHeaders.X_QUERY_LIMIT, "1000");
        final File file = new File("src/test/data/instances/list_under_limit.json");
        final HttpEntity entity = new FileEntity(file);
        response2.setEntity(entity);

        final Queue<HttpResponse> responses = new LinkedList<>(
                ImmutableList.of(response1, response2)
        );

        try (CloudApiConnectionContext context = createMockContext(responses)) {
            Iterator<Instance> itr = instanceApi.list(context);
            assertTrue(itr.hasNext(), "This shouldn't be an empty iterator");

            assertNotNull(itr.next());
            assertNotNull(itr.next());

            assertFalse(itr.hasNext(), "This should be the end of the iterator");
        }
    }

    public void canDeleteInstance() throws IOException {
        final StatusLine statusLine = new BasicStatusLine(HTTP_1_1, HttpStatus.SC_NO_CONTENT, "No Content");
        final HttpResponse response = new BasicHttpResponse(statusLine);

        try (CloudApiConnectionContext context = createMockContext(response)) {
            UUID instanceId = new UUID(512L, 1024L);
            instanceApi.delete(context, instanceId);

            // No errors means it completed successfully
        }
    }

    public void errorsCorrectlyWhenDeletingUnknownInstance() throws IOException {
        final StatusLine statusLine = new BasicStatusLine(HTTP_1_1, HttpStatus.SC_NOT_FOUND, "Not Found");
        final HttpResponse response = new BasicHttpResponse(statusLine);
        final File file = new File("src/test/data/error/not_found.json");
        final HttpEntity entity = new FileEntity(file);
        response.setEntity(entity);

        boolean thrown = false;

        try (CloudApiConnectionContext context = createMockContext(response)) {
            UUID instanceId = new UUID(512L, 1024L);
            instanceApi.delete(context, instanceId);
        } catch (CloudApiResponseException e) {
            thrown = true;
            assertTrue(e.getMessage().contains("VM not found"),
                       "Unexpected message on exception");
        }

        assertTrue(thrown, "CloudApiResponseException never thrown");
    }

    public void canWaitForStateChangeWhenItAlreadyChanged() throws IOException {
        final StatusLine statusLine = new BasicStatusLine(HTTP_1_1, HttpStatus.SC_OK, "OK");
        final HttpResponse response = new BasicHttpResponse(statusLine);
        final File file = new File("src/test/data/domain/instance.json");
        final HttpEntity entity = new FileEntity(file);
        response.setEntity(entity);

        final UUID instanceId = UUID.fromString("c872d3bf-cbaa-4165-8e18-f6e3e1d94da9");
        final String stateToChangeFrom = "provisioning";

        try (CloudApiConnectionContext context = createMockContext(response)) {
            final Instance running = instanceApi.waitForStateChange(
                    context, instanceId, stateToChangeFrom, 0L, 0L);

            assertEquals(running.getId(), instanceId, "ids should match");
            assertNotEquals(running.getState(), stateToChangeFrom,
                    "shouldn't be in [" + stateToChangeFrom + "] state");
            assertEquals(running.getState(), "running", "should be in running state");
        }
    }

    public void canWaitForStateChange() throws IOException {
        final StatusLine statusLine = new BasicStatusLine(HTTP_1_1, HttpStatus.SC_OK, "OK");
        final Queue<HttpResponse> responses = new LinkedList<>();

        {
            final HttpResponse response = new BasicHttpResponse(statusLine);
            final File file = new File("src/test/data/instances/instance_provisioning.json");
            final HttpEntity entity = new FileEntity(file);
            response.setEntity(entity);
            // Imitate going to the server 4 times to poll for changes
            responses.add(response);
            responses.add(response);
            responses.add(response);
            responses.add(response);
        }
        {
            final HttpResponse response = new BasicHttpResponse(statusLine);
            final File file = new File("src/test/data/domain/instance.json");
            final HttpEntity entity = new FileEntity(file);
            response.setEntity(entity);
            responses.add(response);
        }

        final UUID instanceId = UUID.fromString("c872d3bf-cbaa-4165-8e18-f6e3e1d94da9");
        final String stateToChangeFrom = "provisioning";

        try (CloudApiConnectionContext context = createMockContext(responses)) {
            final Instance running = instanceApi.waitForStateChange(
                    context, instanceId, stateToChangeFrom, 500L, 30000L);

            assertEquals(running.getId(), instanceId, "ids should match");
            assertNotEquals(running.getState(), stateToChangeFrom,
                    "shouldn't be in [" + stateToChangeFrom + "] state");
            assertEquals(running.getState(), "running", "should be in running state");
        }
    }

    public void canFindRunningInstanceById() throws IOException {
        final StatusLine statusLine = new BasicStatusLine(HTTP_1_1, HttpStatus.SC_OK, "OK");
        final HttpResponse response = new BasicHttpResponse(statusLine);
        final File file = new File("src/test/data/domain/instance.json");
        final HttpEntity entity = new FileEntity(file);
        response.setEntity(entity);

        final UUID instanceId = UUID.fromString("c872d3bf-cbaa-4165-8e18-f6e3e1d94da9");

        try (CloudApiConnectionContext context = createMockContext(response)) {
            Instance found = instanceApi.findById(context, instanceId);

            assertNotNull(found, "expecting instance to be found");
            assertEquals(found.getId(), instanceId, "expecting ids to match");
        }
    }

    public void canHandleMissingInstanceWhenFindingById() throws IOException {
        final StatusLine statusLine = new BasicStatusLine(HTTP_1_1, HttpStatus.SC_NOT_FOUND, "Not Found");
        final HttpResponse response = new BasicHttpResponse(statusLine);

        final UUID instanceId = new UUID(-1L, -2L);

        try (CloudApiConnectionContext context = createMockContext(response)) {
            Instance found = instanceApi.findById(context, instanceId);

            assertNull(found, "expecting instance to not be found");
        }
    }

    public void canAddAdditionalTagsToAnInstance() throws IOException {
        final StatusLine statusLine = new BasicStatusLine(HTTP_1_1, HttpStatus.SC_OK, "OK");
        final HttpResponse response = new BasicHttpResponse(statusLine);
        final File file = new File("src/test/data/instances/additional_tags.json");
        final HttpEntity entity = new FileEntity(file);
        response.setEntity(entity);

        final UUID instanceId = UUID.fromString("c872d3bf-cbaa-4165-8e18-f6e3e1d94da9");

        try (CloudApiConnectionContext context = createMockContext(response)) {
            Map<String, String> tags = instanceApi.addTags(context, instanceId,
                    ImmutableMap.of("additional_1", "val1", "additional_2", "val2"));

            assertEquals(tags.size(), 3, "Expecting 3 tags");
            assertEquals(tags.get("name"), "value");
            assertEquals(tags.get("additional_1"), "val1");
            assertEquals(tags.get("additional_2"), "val2");
        }
    }

    public void canReplaceTagsOnAnInstance() throws IOException {
        final StatusLine statusLine = new BasicStatusLine(HTTP_1_1, HttpStatus.SC_OK, "OK");
        final HttpResponse response = new BasicHttpResponse(statusLine);
        final File file = new File("src/test/data/instances/replace_tags.json");
        final HttpEntity entity = new FileEntity(file);
        response.setEntity(entity);

        final UUID instanceId = UUID.fromString("c872d3bf-cbaa-4165-8e18-f6e3e1d94da9");

        try (CloudApiConnectionContext context = createMockContext(response)) {
            Map<String, String> tags = instanceApi.replaceTags(context, instanceId,
                    ImmutableMap.of("additional_1", "val1", "additional_2", "val2"));

            assertEquals(tags.size(), 2, "Expecting 2 tags");
            assertEquals(tags.get("additional_1"), "val1");
            assertEquals(tags.get("additional_2"), "val2");
        }
    }
}
