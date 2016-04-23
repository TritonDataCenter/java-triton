package com.joyent.triton.queryfilters;

/**
 * Interface that marks a class as being a filter that can be converted
 * to a set of URL query parameters.
 *
 * @author <a href="https://github.com/dekobon">Elijah Zupancic</a>
 * @since 1.0.0
 */
public interface QueryFilter {
    /**
     * Method that checks to see if any filter parameters are set.
     *
     * @return true if there are no filter parameters set
     */
    boolean isEmpty();
}
