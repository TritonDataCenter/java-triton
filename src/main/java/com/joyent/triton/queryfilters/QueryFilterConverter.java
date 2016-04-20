package com.joyent.triton.queryfilters;

import org.apache.http.NameValuePair;

import java.util.List;

/**
 * Interface providing the contract for how a {@link QueryFilter} object is
 * converted to a set of query parameters.
 *
 * @param <T> {@link QueryFilter} class implementation
 */
public interface QueryFilterConverter<T extends QueryFilter> {
    /**
     * Converts a {@link QueryFilter} object to a list of query
     * parameters that are sent as part of a HTTP request to the
     * CloudAPI.
     *
     * @param filter query filter object
     * @return list of NameValuePairs used by the Apache HTTP Client
     */
    List<NameValuePair> urlParamsFromFilter(T filter);
}
