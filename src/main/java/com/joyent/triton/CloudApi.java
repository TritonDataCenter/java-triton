package com.joyent.triton;

import com.joyent.triton.config.ConfigContext;
import com.joyent.triton.http.CloudApiApacheHttpClientContext;
import com.joyent.triton.http.CloudApiConnectionContext;
import com.joyent.triton.http.CloudApiConnectionFactory;

/**
 * Class providing central functionality useful when interacting with all of
 * the other CloudAPI classes ({@link Instances}, etc).
 *
 * @author <a href="https://github.com/dekobon">Elijah Zupancic</a>
 * @since 1.0.0
 */
public class CloudApi {
    /**
     * Configuration context that provides SDK settings.
     */
    private final ConfigContext config;

    /**
     * {@link org.apache.http.impl.client.CloseableHttpClient} connection factory.
     */
    private final CloudApiConnectionFactory connectionFactory;

    /**
     * Reference to Instances API section.
     */
    private final Instances instances;

    /**
     * Creates a new instance based on the passed configuration.
     * @param config SDK configuration object
     */
    public CloudApi(final ConfigContext config) {
        this.config = config;
        this.connectionFactory = new CloudApiConnectionFactory(config);
        this.instances = new Instances(this);
    }

    public CloudApiConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    /**
     * Creates a new connection context that provides resources that can
     * be shared across API calls.
     *
     * @return new context object
     */
    public CloudApiConnectionContext createConnectionContext() {
        return new CloudApiApacheHttpClientContext(connectionFactory);
    }

    /**
     * Provides access to the Instances API.
     *
     * @return a references to a configured {@link Instances} object.
     */
    public Instances instances() {
        return instances;
    }

    /**
     * Default scoped getter that feeds API section classes (like Instances).
     * @return instance of configuration class
     */
    ConfigContext getConfig() {
        return config;
    }
}
