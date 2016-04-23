package com.joyent.triton.queryfilters;

import org.apache.http.NameValuePair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utility class that provides converters from filter objects to lists of
 * {@link NameValuePair} objects so that the filters can be encoded as URL
 * parameters easily.
 *
 * @author <a href="https://github.com/dekobon">Elijah Zupancic</a>
 * @since 1.0.0
 */
public class PackageFilterConverter extends BaseQueryFilterConverter<PackageFilter> {
    /**
     * Creates a new instance.
     */
    public PackageFilterConverter() {
    }

    @Override
    public List<NameValuePair> urlParamsFromFilter(final PackageFilter filter) {
        if (filter.isEmpty()) {
            return Collections.emptyList();
        }

        final List<NameValuePair> urlParams = new ArrayList<>();

        addIfSet(urlParams, "name", filter.getName());
        addIfSet(urlParams, "memory", filter.getMemory());
        addIfSet(urlParams, "disk", filter.getDisk());
        addIfSet(urlParams, "swap", filter.getSwap());
        addIfSet(urlParams, "lwps", filter.getLwps());
        addIfSet(urlParams, "vcpus", filter.getVcpus());
        addIfSet(urlParams, "version", filter.getVersion());
        addIfSet(urlParams, "group", filter.getGroup());

        return urlParams;
    }
}
