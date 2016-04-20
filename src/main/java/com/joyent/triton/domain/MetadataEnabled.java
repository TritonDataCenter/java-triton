package com.joyent.triton.domain;

import java.util.Map;

/**
 * Interface indicating that a given domain object has the feature of a
 * free-form metadata.
 *
 * @param <T> type of the implementing object
 */
public interface MetadataEnabled<T> {
    /**
     * @return returns a map of metadata
     */
    Map<String, String> getMetadata();

    /**
     * Assigns the passed map of metadata.
     * @param metadata map of metadata to assign
     * @return reference to the underlying implementing object
     */
    T setMetadata(final Map<String, String> metadata);
}
