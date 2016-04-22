package com.joyent.triton.exceptions;

/**
 * Exception class that indicates there was a problem authenticating against the CloudAPI.
 *
 * @author <a href="https://github.com/dekobon">Elijah Zupancic</a>
 * @since 1.0.0
 */
public class CloudApiAuthenticationException extends CloudApiIOException {

    private static final long serialVersionUID = 5557608527088148775L;

    /**
     * Create a new instance with the default message.
     */
    public CloudApiAuthenticationException() {
        super("Unable to authenticated against CloudAPI. Check credentials.");
    }
}
