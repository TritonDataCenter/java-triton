package com.joyent.triton;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.joyent.triton.config.ConfigContext;
import com.joyent.triton.exceptions.CloudApiException;
import com.joyent.triton.exceptions.CloudApiIOException;
import com.joyent.triton.http.CloudApiConnectionContext;
import com.joyent.triton.http.CloudApiConnectionFactory;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;

/**
 * Abstract class that provides useful methods for CloudAPI accessor classes.
 *
 * @author <a href="https://github.com/dekobon">Elijah Zupancic</a>
 * @since 1.0.0
 */
public abstract class BaseApiAccessor {
    /**
     * Configuration context that provides SDK settings.
     */
    private final ConfigContext config;

    /**
     * Reference to main API class used for creating connections.
     */
    private final CloudApi cloudApi;

    /**
     * {@link org.apache.http.impl.client.CloseableHttpClient} connection factory.
     */
    private final CloudApiConnectionFactory connectionFactory;

    /**
     * Customized Jackson serialization/deserialization object.
     */
    private final ObjectMapper mapper;

    /**
     * Creates a new parent instance.
     *
     * @param cloudApi reference to {@link CloudApi} instance that is backing API calls.
     * @param mapper reference to the jackson object mapper to use for processing JSON
     */
    protected BaseApiAccessor(final CloudApi cloudApi,
                           final ObjectMapper mapper) {
        this.cloudApi = cloudApi;
        this.config = cloudApi.getConfig();
        this.connectionFactory = new CloudApiConnectionFactory(getConfig());
        this.mapper = mapper;
    }

    /**
     * Executes a HTTP request using the passed context and handler while
     * wrapping exceptions and adding additional context to exceptions.
     *
     * @param context request context used for sharing resources between API operations
     * @param request HTTP request object
     * @param responseHandler HTTP response handler
     * @param <T> type returned by response handler
     * @return result of response handler
     * @throws IOException thrown when we have a problem executing the request
     */
    protected <T> T execute(final CloudApiConnectionContext context,
                         final HttpUriRequest request,
                         final ResponseHandler<? extends T> responseHandler)
            throws IOException {
        try {
            final HttpClient client = context.getHttpClient();
            final HttpContext httpContext = context.getHttpContext();
            return client.execute(request, responseHandler, httpContext);
        } catch (CloudApiIOException | CloudApiException e) {
            CloudApiUtils.annotateContextedException(e, request);
            throw e;
        } catch (IOException e) {
            final String msg = "Error making request to CloudAPI.";
            final CloudApiIOException exception = new CloudApiIOException(msg, e);
            CloudApiUtils.annotateContextedException(exception, request);
            throw exception;
        }
    }

    /**
     * Configuration context that provides SDK settings.
     * @return configuration object
     */
    public ConfigContext getConfig() {
        return config;
    }

    /**
     * Reference to main API class used for creating connections.
     * @return central API accessor object
     */
    public CloudApi getCloudApi() {
        return cloudApi;
    }

    /**
     * {@link org.apache.http.impl.client.CloseableHttpClient} connection factory.
     * @return HTTP client used to open and close connections to API
     */
    public CloudApiConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    /**
     * Customized Jackson serialization/deserialization object.
     * @return a JSON object mapper configured for use with the domain objects
     */
    public ObjectMapper getMapper() {
        return mapper;
    }
}
