package com.joyent.triton.http;

import org.apache.http.client.HttpClient;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;

/**
 * Interface describing the contract for a context class that stores state between
 * requests to the CloudAPI.
 *
 * @author <a href="https://github.com/dekobon">Elijah Zupancic</a>
 * @since 1.0.0
 */
public interface CloudApiConnectionContext extends AutoCloseable {
    /**
     * HTTP client object used for accessing the CloudAPI.
     * @return connection object to the CloudAPI
     */
    HttpClient getHttpClient();

    /**
     * HTTP context object used to share state between HTTP requests.
     * @return HTTP connection context object
     */
    HttpContext getHttpContext();

    @Override
    void close() throws IOException;
}
