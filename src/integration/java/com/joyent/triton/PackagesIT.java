package com.joyent.triton;

import com.joyent.triton.config.ChainedConfigContext;
import com.joyent.triton.config.ConfigContext;
import com.joyent.triton.config.DefaultsConfigContext;
import com.joyent.triton.config.SystemSettingsConfigContext;
import com.joyent.triton.http.CloudApiConnectionContext;
import com.joyent.triton.domain.Package;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

@Test(groups = { "integration" })
public class PackagesIT {
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
}
