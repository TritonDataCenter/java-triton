package com.joyent.triton.http;


import com.joyent.triton.config.ConfigContext;
import com.joyent.triton.exceptions.CloudApiResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

/**
 * Implementation of {@link HttpRequestRetryHandler} customized for use with the
 * CloudAPI.
 *
 * @author <a href="https://github.com/dekobon">Elijah Zupancic</a>
 * @since 1.0.0
 */
public class CloudApiHttpRequestRetryHandler extends DefaultHttpRequestRetryHandler {
    /**
     * Logger instance.
     */
    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * List of all exception types that can't be retried.
     */
    protected static final List<Class<? extends IOException>> NON_RETRIABLE = Arrays.asList(
            CloudApiResponseException.class,
            InterruptedIOException.class,
            UnknownHostException.class,
            ConnectException.class,
            SSLException.class);

    /**
     * Creates a new instance with the passed configuration.
     *
     * @param config configuration for retries
     */
    public CloudApiHttpRequestRetryHandler(final ConfigContext config) {
        super(config.getRetries(), true, NON_RETRIABLE);
    }

    @Override
    public boolean retryRequest(final IOException exception,
                                final int executionCount,
                                final HttpContext context) {
        if (logger.isDebugEnabled()) {
            String msg = String.format("Request failed, %d retry.", executionCount);
            logger.debug(msg, exception);
        }

        return super.retryRequest(exception, executionCount, context);
    }
}
