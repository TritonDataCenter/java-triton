package com.joyent.triton.exceptions;

import org.apache.http.HttpResponse;
import static com.joyent.triton.http.CloudApiHttpHeaders.REQUEST_ID;

/**
 * Exception class indicating that there was a server error on a request to
 * the CloudAPI and we were given a response without much debuggable
 * information.
 */
public class CloudApiRemoteServerException extends CloudApiIOException {
    /**
     * Creates a new instance.
     *
     * @param response the HTTP response from the server associated with the error
     */
    public CloudApiRemoteServerException(final HttpResponse response) {
        super(buildMessage(response));
        addContextValue("response", response);
        addContextValue("requestId", response.getFirstHeader(REQUEST_ID));
    }

    /**
     * Creates a new instance.
     *
     * @param response the HTTP response from the server associated with the error
     * @param cause exception to chain to this exception as a cause
     */
    public CloudApiRemoteServerException(final HttpResponse response, final Exception cause) {
        super(buildMessage(response), cause);
        addContextValue("response", response);
        addContextValue("requestId", response.getFirstHeader(REQUEST_ID));
    }

    /**
     * Generates an error message based on the contents of a HTTP response
     * object.
     *
     * @param response the HTTP response from the server associated with the error
     *
     * @return a generated error message
     */
    private static String buildMessage(final HttpResponse response) {
        return String.format("Remote server error [%d] - %s",
                response.getStatusLine().getStatusCode(),
                response.getStatusLine().getReasonPhrase());
    }
}
