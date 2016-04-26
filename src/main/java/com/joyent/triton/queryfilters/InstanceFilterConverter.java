package com.joyent.triton.queryfilters;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Utility class that provides converters from filter objects to lists of
 * {@link NameValuePair} objects so that the filters can be encoded as URL
 * parameters easily.
 *
 * @author <a href="https://github.com/dekobon">Elijah Zupancic</a>
 * @since 1.0.0
 */
public class InstanceFilterConverter extends BaseQueryFilterConverter<InstanceFilter> {
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

        addIfSet(urlParams, "brand", filter.getBrand());
        addIfSet(urlParams, "name", filter.getName());
        addIfSet(urlParams, "image", filter.getImage());
        addIfSet(urlParams, "state", filter.getState());
        addIfSet(urlParams, "memory", filter.getMemory());
        addIfSet(urlParams, "tombstone", filter.getTombstone());
        addIfSet(urlParams, "limit", filter.getLimit());
        addIfSet(urlParams, "offset", filter.getOffset());
        addIfSet(urlParams, "docker", filter.isDockerOnlyListed());
        addIfSet(urlParams, "credentials", filter.isCredentialsIncluded());

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
