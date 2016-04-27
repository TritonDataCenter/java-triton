package com.joyent.triton.domain;

import com.joyent.triton.CloudApiUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Domain object representing the files property on an {@link Image}.
 *
 * @author <a href="https://github.com/dekobon">Elijah Zupancic</a>
 * @since 1.0.0
 */
public class ImageFiles implements Entity {

    /**
     * The type of file compression used for the image file. One of 'bzip2', 'gzip', 'none'.
     */
    private String compression;

    /**
     * SHA-1 hex digest of the file content. Used for corruption checking.
     */
    private String sha1;

    /**
     * File size in bytes.
     */
    private long size;

    /**
     * Creates a new instance that can be configured using a fluent interface.
     */
    public ImageFiles() {
    }

    @Override
    public Map<String, Object> asMap() {
        final Map<String, Object> attributes = new LinkedHashMap<>();

        if (getCompression() != null) {
            attributes.put("compression", getCompression());
        }

        if (getSha1() != null) {
            attributes.put("sha1", getSha1());
        }

        attributes.put("size", getSize());

        return Collections.unmodifiableMap(attributes);
    }

    @Override
    public Map<String, String> asStringMap() {
        final Map<String, Object> map = asMap();

        return CloudApiUtils.asStringMap(map);
    }

    public String getCompression() {
        return compression;
    }

    public ImageFiles setCompression(final String compression) {
        this.compression = compression;
        return this;
    }

    public String getSha1() {
        return sha1;
    }

    public ImageFiles setSha1(final String sha1) {
        this.sha1 = sha1;
        return this;
    }

    public long getSize() {
        return size;
    }

    public ImageFiles setSize(final long size) {
        this.size = size;
        return this;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ImageFiles that = (ImageFiles) o;
        return size == that.size
                && Objects.equals(compression, that.compression)
                && Objects.equals(sha1, that.sha1);
    }

    @Override
    public int hashCode() {
        return Objects.hash(compression, sha1, size);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("compression", compression)
                .append("sha1", sha1)
                .append("size", size)
                .toString();
    }
}
