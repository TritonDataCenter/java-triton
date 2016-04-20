package com.joyent.triton.queryfilters;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.joyent.triton.CloudApiUtils.toStringEmptyToNull;

/**
 * Utility class that provides converters from filter objects to lists of
 * {@link NameValuePair} objects so that the filters can be encoded as URL
 * parameters easily.
 *
 * @author <a href="https://github.com/dekobon">Elijah Zupancic</a>
 * @since 1.0.0
 */
public class InstanceFilterConverter implements QueryFilterConverter<InstanceFilter> {
    /**
     * Creates a new instance.
     */
    public InstanceFilterConverter() {
    }

    /**
     * Converts a {@link InstanceFilter} to a set of URL parameters.
     * @param filter instance to convert
     * @return list of url parameters to pass as a filter
     */
    @Override
    public List<NameValuePair> urlParamsFromFilter(final InstanceFilter filter) {
        if (filter.isEmpty()) {
            return Collections.emptyList();
        }

        final List<NameValuePair> urlParams = new ArrayList<>();

        if (toStringEmptyToNull(filter.getBrand()) != null) {
            urlParams.add(new BasicNameValuePair("brand", filter.getBrand()));
        }
        if (toStringEmptyToNull(filter.getName()) != null) {
            urlParams.add(new BasicNameValuePair("name", filter.getName()));
        }
        if (toStringEmptyToNull(filter.getImage()) != null) {
            urlParams.add(new BasicNameValuePair("image", filter.getImage().toString()));
        }
        if (toStringEmptyToNull(filter.getState()) != null) {
            urlParams.add(new BasicNameValuePair("state", filter.getState()));
        }
        if (toStringEmptyToNull(filter.getMemory()) != null) {
            urlParams.add(new BasicNameValuePair("memory", String.valueOf(filter.getMemory())));
        }
        if (toStringEmptyToNull(filter.getTombstone()) != null) {
            urlParams.add(new BasicNameValuePair("tombstone", String.valueOf(filter.getTombstone())));
        }
        if (toStringEmptyToNull(filter.getLimit()) != null) {
            urlParams.add(new BasicNameValuePair("limit", String.valueOf(filter.getLimit())));
        }
        if (toStringEmptyToNull(filter.getOffset()) != null) {
            urlParams.add(new BasicNameValuePair("offset", String.valueOf(filter.getOffset())));
        }
        if (filter.isDockerOnlyListed() != null) {
            urlParams.add(new BasicNameValuePair("docker", String.valueOf(filter.isDockerOnlyListed())));
        }
        if (filter.isCredentialsIncluded() != null) {
            urlParams.add(new BasicNameValuePair("credentials", String.valueOf(filter.isCredentialsIncluded())));
        }

        if (filter.getTags() != null && !filter.getTags().isEmpty()) {
            final Set<Map.Entry<String, String>> entries = filter.getTags().entrySet();
            for (Map.Entry<String, String> entry : entries) {
                final String key = String.format("tag.%s", entry.getKey());
                final NameValuePair pair = new BasicNameValuePair(key, entry.getValue());
                urlParams.add(pair);
            }
        }

        return urlParams;
    }
}
