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
import java.util.Iterator;

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
            final Iterator<Package> packages = packagesApi.list(context);

            while (packages.hasNext()) {
                Package next = packages.next();
                System.out.println(next);
            }
        }
    }
}
