package com.joyent.triton;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.joyent.triton.config.ConfigContext;
import com.joyent.triton.http.CloudApiApacheHttpClientContext;
import com.joyent.triton.http.CloudApiConnectionContext;
import com.joyent.triton.http.CloudApiConnectionFactory;
import com.joyent.triton.json.CloudApiObjectMapper;

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
     * Reference to the Packages API section.
     */
    private final Packages packages;

    /**
     * Reference to the Images API section.
     */
    private final Images images;

    /**
     * Creates a new instance based on the passed configuration.
     * @param config SDK configuration object
     */
    public CloudApi(final ConfigContext config) {
        final ObjectMapper mapper = new CloudApiObjectMapper();

        this.config = config;
        this.connectionFactory = new CloudApiConnectionFactory(config);
        this.instances = new Instances(this, mapper);
        this.packages = new Packages(this, mapper);
        this.images = new Images(this, mapper);
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
     * Provides access to the Packages API.
     *
     * @return a references to a configured {@link Packages} object.
     */
    public Packages packages() {
        return packages;
    }

    /**
     * Provides access to the Images API.
     *
     * @return a references to a configured {@link Images} object.
     */
    public Images images() {
        return images;
    }

    /**
     * Default scoped getter that feeds API section classes (like Instances).
     * @return instance of configuration class
     */
    ConfigContext getConfig() {
        return config;
    }
}
