package com.joyent.triton;

import com.joyent.triton.config.ChainedConfigContext;
import com.joyent.triton.config.ConfigContext;
import com.joyent.triton.config.DefaultsConfigContext;
import com.joyent.triton.config.SystemSettingsConfigContext;
import com.joyent.triton.http.CloudApiConnectionContext;
import com.joyent.triton.domain.Package;
import com.joyent.triton.queryfilters.PackageFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.collections.Lists;

import java.io.IOException;
import java.util.*;

import static org.testng.Assert.*;

@Test(groups = { "integration" })
public class PackagesIT {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private ConfigContext config = null;
    private Packages packagesApi = null;
    private CloudApi cloudApi = null;

    @BeforeClass
    public void setup() {
        this.config = new ChainedConfigContext(
                new DefaultsConfigContext(),
                new SystemSettingsConfigContext()
        );
        this.cloudApi = new CloudApi(config);
        this.packagesApi = cloudApi.packages();
    }

    public void canListPackages() throws IOException {
        try (CloudApiConnectionContext context = cloudApi.createConnectionContext()) {
            final Collection<Package> packages = packagesApi.list(context);

            assertFalse(packages.isEmpty(), "There must be at least a single package");

            final Set<Package> packageSet = new HashSet<>(packages);
            assertEquals(packageSet.size(), packages.size(),
                    "There should be no duplicate packages");
        }
    }

    @Test(dependsOnMethods = "canListPackages")
    public void canFilterPackages() throws IOException {
        try (CloudApiConnectionContext context = cloudApi.createConnectionContext()) {
            final long expectedMemory = 16_384L;
            PackageFilter pf = new PackageFilter()
                    .setMemory(expectedMemory);

            final Collection<Package> packages = packagesApi.list(context, pf);

            if (packages.isEmpty()) {
                String msg = "Verify that there is at least a single 16G package";
                throw new SkipException(msg);
            }

            for (Package pkg : packages) {
                logger.debug("Found package: {}", pkg.getName());

                assertEquals(pkg.getMemory(), expectedMemory,
                        "Memory amount didn't match filter. Packages:\n" + packages);
            }
        }
    }

    public void verifyNonexistentPackageReturnsAsNull() throws IOException {
        try (CloudApiConnectionContext context = cloudApi.createConnectionContext()) {
            final UUID badPackageId = new UUID(-1L, -1L);
            final Package result = packagesApi.findById(context, badPackageId);
            assertNull(result, "When a package isn't found, it should be null");
        }
    }

    @Test(dependsOnMethods = "canListPackages")
    public void canFindPackageById() throws IOException {
        try (CloudApiConnectionContext context = cloudApi.createConnectionContext()) {
            final List<Package> packages = Lists.newArrayList(packagesApi.list(context));

            assertFalse(packages.isEmpty(), "There must be at least a single package");

            Collections.shuffle(packages);

            final UUID packageId = packages.iterator().next().getId();
            final Package pkg = packagesApi.findById(packageId);
            assertNotNull(pkg, "There must be a package returned");
            assertEquals(pkg.getId(), packageId, "Package ids must match");
        }
    }
}
