package com.joyent.triton.http;

/**
 * CloudAPI specific globally used HTTP header constants.
 *
 * @author <a href="https://github.com/dekobon">Elijah Zupancic</a>
 * @since 1.0.0
 */
public final class CloudApiHttpHeaders {
    /**
     * This class is used only for constants.
     */
    private CloudApiHttpHeaders() { }
    /**
     * HTTP request header sent to identify a unique request.
     */
    public static final String X_REQUEST_ID = "x-request-id";

    /**
     * HTTP response header indicating the unique id of request made to the CloudAPI.
     */
    public static final String REQUEST_ID = "request-id";

    /**
     * HTTP response header indicating the total number of records returned from
     * the CloudAPI.
     */
    public static final String X_RESOURCE_COUNT = "x-resource-count";

    /**
     * HTTP response header indicating the maximum number of records that can
     * be returned per request.
     */
    public static final String X_QUERY_LIMIT = "x-query-limit";
}
