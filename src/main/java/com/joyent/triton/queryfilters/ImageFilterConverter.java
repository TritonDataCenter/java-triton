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
public class ImageFilterConverter extends BaseQueryFilterConverter<ImageFilter> {
    /**
     * Creates a new instance.
     */
    public ImageFilterConverter() {
    }

    @Override
    public List<NameValuePair> urlParamsFromFilter(final ImageFilter filter) {
        if (filter.isEmpty()) {
            return Collections.emptyList();
        }

        final List<NameValuePair> urlParams = new ArrayList<>();

        addIfSet(urlParams, "name", filter.getName());
        addIfSet(urlParams, "os", filter.getOs());
        addIfSet(urlParams, "owner", filter.getOwner());
        addIfSet(urlParams, "version", filter.getVersion());
        addIfSet(urlParams, "public", filter.getPubliclyAvailable());
        addIfSet(urlParams, "state", filter.getState());
        addIfSet(urlParams, "type", filter.getType());

        return urlParams;
    }
}
