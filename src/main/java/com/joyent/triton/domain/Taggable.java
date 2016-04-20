package com.joyent.triton.domain;

import java.util.Map;

/**
 * Interface indicating that a given domain object has the feature of a
 * tag-based taxonomy.
 *
 * @param <T> type of the implementing object
 */
public interface Taggable<T> {
    /**
     * @return returns a map of tags
     */
    Map<String, String> getTags();

    /**
     * Assigns the passed map of tags.
     * @param tags map of tags to assign
     * @return reference to the underlying implementing object
     */
    T setTags(final Map<String, String> tags);
}
