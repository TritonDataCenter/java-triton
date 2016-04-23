package com.joyent.triton.domain;

import com.joyent.triton.CloudApiUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Class representing the affinity or locality of one instance to another
 * instance. Basically, this means how close or far two compute instances are
 * physically from each other.
 *
 * @author <a href="https://github.com/dekobon">Elijah Zupancic</a>
 * @since 1.0.0
 */
public class Locality implements Entity {
    /**
     * Java object serialization id.
     */
    private static final long serialVersionUID = 4365452882708818362L;

    /**
     * Flag indicating provisioning will happen strictly when the criteria are met.
     */
    private boolean strict;

    /**
     * Controls whether the instance should be placed close to these instances.
     */
    private Set<UUID> near;

    /**
     * Controls whether the instance should be placed far from these instances.
     */
    private Set<UUID> far;

    /**
     * Creates a new instance. Use the fluent interface to set properties.
     */
    public Locality() {
    }

    @Override
    public Map<String, Object> asMap() {
        final Map<String, Object> attributes = new LinkedHashMap<>();

        if (getFar() != null) {
            attributes.put("far", getFar());
        }

        if (getNear() != null) {
            attributes.put("near", getNear());
        }

        attributes.put("is_strict", isStrict());

        return Collections.unmodifiableMap(attributes);
    }

    @Override
    public Map<String, String> asStringMap() {
        final Map<String, Object> map = asMap();

        return CloudApiUtils.asStringMap(map);
    }

    public boolean isStrict() {
        return strict;
    }

    public Locality setStrict(final boolean strict) {
        this.strict = strict;
        return this;
    }

    public Set<UUID> getNear() {
        return near;
    }

    public Locality setNear(final Set<UUID> near) {
        this.near = near;
        return this;
    }

    public Set<UUID> getFar() {
        return far;
    }

    public Locality setFar(final Set<UUID> far) {
        this.far = far;
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
        final Locality locality = (Locality) o;

        return strict == locality.strict
                && Objects.equals(near, locality.near)
                && Objects.equals(far, locality.far);
    }

    @Override
    public int hashCode() {
        return Objects.hash(strict, near, far);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("strict", strict)
                .append("near", near)
                .append("far", far)
                .toString();
    }
}
