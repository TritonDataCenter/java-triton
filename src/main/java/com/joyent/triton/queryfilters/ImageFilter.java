package com.joyent.triton.queryfilters;

import com.joyent.triton.domain.Image;

import java.util.UUID;

/**
 * A {@link QueryFilter} implementation that provides a means for querying
 * specific sets of {@link Image} classes from
 * CloudAPI.
 *
 * @author <a href="https://github.com/dekobon">Elijah Zupancic</a>
 * @since 1.0.0
 */
public class ImageFilter implements QueryFilter {
    /**
     * The "friendly" name for this package.
     */
    private String name;

    /**
     * The underlying operating system for this image.
     */
    private String os;

    /**
     * The version for this image.
     */
    private String version;

    /**
     * Filter public/private images.
     */
    private Boolean publiclyAvailable;

    /**
     * Filter on image state. By default only active images are shown.
     * Use state=all to list all images.
     */
    private String state;

    /**
     * Filter on owner UUID.
     */
    private UUID owner;

    /**
     * Filter on image type.
     */
    private String type;

    /**
     * Creates a new instance. This class can be configured by a fluent
     * interface.
     */
    public ImageFilter() {
    }

    public String getName() {
        return name;
    }

    public ImageFilter setName(final String name) {
        this.name = name;
        return this;
    }

    public String getOs() {
        return os;
    }

    public ImageFilter setOs(final String os) {
        this.os = os;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public ImageFilter setVersion(final String version) {
        this.version = version;
        return this;
    }

    public Boolean getPubliclyAvailable() {
        return publiclyAvailable;
    }

    public ImageFilter setPubliclyAvailable(final Boolean publiclyAvailable) {
        this.publiclyAvailable = publiclyAvailable;
        return this;
    }

    public String getState() {
        return state;
    }

    public ImageFilter setState(final String state) {
        this.state = state;
        return this;
    }

    public UUID getOwner() {
        return owner;
    }

    public ImageFilter setOwner(final UUID owner) {
        this.owner = owner;
        return this;
    }

    public String getType() {
        return type;
    }

    public ImageFilter setType(final String type) {
        this.type = type;
        return this;
    }

    @Override
    public boolean isEmpty() {
        return getName() == null
                && getOs() == null
                && getOwner() == null
                && getPubliclyAvailable() == null
                && getState() == null
                && getType() == null
                && getVersion() == null;
    }
}
