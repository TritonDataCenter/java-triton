package com.joyent.triton.http;

import com.joyent.http.signature.apache.httpclient.HttpSignatureConfigurator;
import org.apache.http.auth.AuthProtocolState;
import org.apache.http.auth.AuthState;
import org.apache.http.client.AuthCache;
import org.apache.http.client.HttpClient;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.HttpContext;
import org.slf4j.MDC;

import java.io.IOException;
import java.util.Objects;

/**
 * {@link AutoCloseable} class that encapsulates the functionality of multiple
 * closeable resources that are used when accessing the CloudAPI. This class can
 * be passed to any method that directly accesses the CloudAPI in order to more
 * efficiently use network resources.
 *
 * @author <a href="https://github.com/dekobon">Elijah Zupancic</a>
 * @since 1.0.0
 */
public class CloudApiApacheHttpClientContext implements CloudApiConnectionContext {
    /**
     * HTTP client object used for accessing the CloudAPI.
     */
    private final HttpClient httpClient;

    /**
     * HTTP context object used to share state between HTTP requests.
     */
    private final HttpContext httpContext;

    /**
     * Creates a new instance using the passed in factory class.
     * @param connectionFactory factory class that creates configured connections
     */
    public CloudApiApacheHttpClientContext(final CloudApiConnectionFactory connectionFactory) {
        Objects.requireNonNull(connectionFactory,
                "Connection factory must be present");

        this.httpClient = connectionFactory.createConnection();
        this.httpContext = buildHttpContext(connectionFactory.getSignatureConfigurator());
    }

    /**
     * Builds a configured HTTP context object that is pre-configured for
     * using HTTP Signature authentication.
     *
     * @param configurator HTTP Signatures configuration helper to pull properties from
     * @return configured HTTP context object
     */
    protected HttpContext buildHttpContext(final HttpSignatureConfigurator configurator) {
        final HttpClientContext context = HttpClientContext.create();

        if (configurator != null) {
            AuthCache authCache = new BasicAuthCache();
            context.setAuthCache(authCache);

            AuthState authState = new AuthState();
            authState.update(configurator.getAuthScheme(), configurator.getCredentials());

            context.setAttribute(HttpClientContext.TARGET_AUTH_STATE,
                    authState);
            context.getTargetAuthState().setState(AuthProtocolState.UNCHALLENGED);

        }

        return context;
    }

    @Override
    public HttpClient getHttpClient() {
        return httpClient;
    }

    @Override
    public HttpContext getHttpContext() {
        return httpContext;
    }

    @Override
    public void close() throws IOException {
        MDC.remove(RequestIdInterceptor.MDC_REQUEST_ID_STRING);

        if (httpClient instanceof CloseableHttpClient) {
            ((CloseableHttpClient)httpClient).close();
        }
    }
}
