package com.joyent.triton.domain;

import com.joyent.triton.CloudApiUtils;
import com.joyent.triton.exceptions.CloudApiException;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import static org.bouncycastle.crypto.tls.MACAlgorithm.sha;

/**
 * Domain object representing the files property on an {@link Image}.
 *
 * @author <a href="https://github.com/dekobon">Elijah Zupancic</a>
 * @since 1.0.0
 */
public class ImageFiles implements Entity {

    private static final long serialVersionUID = 309357437150257452L;

    /**
     * The type of file compression used for the image file. One of 'bzip2', 'gzip', 'none'.
     */
    private String compression;

    /**
     * SHA-1 hex digest of the file content. Used for corruption checking.
     */
    private byte[] sha1;

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

    /**
     * The SHA1 checksum as a string.
     * @return lowercase hex string of sha1 checksum
     */
    public String getSha1() {
        if (sha1 != null) {
            return new String(Hex.encodeHex(sha1, true));
        }

        return null;
    }

    public byte[] getSha1Bytes() {
        return this.sha1;
    }

    public ImageFiles setSha1(final String sha1) {
        try {
            this.sha1 = Hex.decodeHex(sha1.toCharArray());
        } catch (DecoderException e) {
            CloudApiException exception = new CloudApiException(
                    "Unable to decode hex string", e);
            exception.setContextValue("hexString", sha1);

            throw exception;
        }

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

        final ImageFiles that = (ImageFiles) o;

        return size == that.size
                && Objects.equals(compression, that.compression)
                && Arrays.equals(sha1, that.sha1);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(size)
                .append(compression)
                .append(sha)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("compression", compression)
                .append("sha1", getSha1())
                .append("size", size)
                .toString();
    }
}
