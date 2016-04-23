package com.joyent.triton.queryfilters;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.List;

import static com.joyent.triton.CloudApiUtils.toStringEmptyToNull;

/**
 * Abstract class that provides helpers for converting filter objects to
 * lists of {@link NameValuePair} objects.
 *
 * @param <T> {@link QueryFilter} class implementation
 * @author <a href="https://github.com/dekobon">Elijah Zupancic</a>
 * @since 1.0.0
 */
public abstract class BaseQueryFilterConverter<T extends QueryFilter>
        implements QueryFilterConverter<T> {

    /**
     * Adds the passed key and value as string to the URL parameters
     * if the value is not empty.
     *
     * @param urlParams list to add to
     * @param name name of parameter
     * @param value value of parameter
     */
    protected static void addIfSet(final List<NameValuePair> urlParams,
                                   final String name,
                                   final Object value) {
        if (toStringEmptyToNull(value) == null) {
            return;
        }

        final String stringValue;

        if (value instanceof String) {
            stringValue = (String)value;
        } else if (value instanceof Number) {
            stringValue = String.valueOf(value);
        } else {
            stringValue = value.toString();
        }

        final BasicNameValuePair pair = new BasicNameValuePair(name, stringValue);
        urlParams.add(pair);
    }
}
