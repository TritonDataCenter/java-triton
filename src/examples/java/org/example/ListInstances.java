package org.example;

import com.joyent.triton.CloudApi;
import com.joyent.triton.Instances;
import com.joyent.triton.config.ChainedConfigContext;
import com.joyent.triton.config.ConfigContext;
import com.joyent.triton.config.DefaultsConfigContext;
import com.joyent.triton.config.EnvVarConfigContext;
import com.joyent.triton.config.SystemSettingsConfigContext;
import com.joyent.triton.domain.Instance;

import java.io.IOException;
import java.util.Iterator;

public class ListInstances {
    public static void main(String argv) throws IOException {
        /* This configures the Triton SDK using defaults, environment variables
         * and Java system properties. */
        ConfigContext context = new ChainedConfigContext(
                new DefaultsConfigContext(),
                new EnvVarConfigContext(),
                new SystemSettingsConfigContext()
        );

        CloudApi cloudApi = new CloudApi(context);

        // Each section of the API has its own class
        Instances instanceApi = cloudApi.instances();

        Iterator<Instance> instancesIterator = instanceApi.list();

        while (instancesIterator.hasNext()) {
            Instance instance = instancesIterator.next();
            System.out.println(instance);
        }
    }
}
