package com.joyent.triton.domain;

import com.joyent.triton.CloudApiUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain object representing a compute package - essentially an instance
 * type.
 *
 * @author <a href="https://github.com/dekobon">Elijah Zupancic</a>
 * @since 1.0.0
 */
public class Package implements Entity {
    /**
     * Java object serialization id.
     */
    private static final long serialVersionUID = 7182366089017804641L;

    /**
     * Unique id for this package.
     */
    private UUID id;

    /**
     * The "friendly" name for this package.
     */
    private String name;

    /**
     * How much memory will by available (in MiB).
     */
    private long memory;

    /**
     * How much disk space will be available (in MiB).
     */
    private long disk;

    /**
     * How much swap space will be available (in MiB).
     */
    private long swap;

    /**
     * Number of vCPUs for this package.
     */
    private int vcpus;

    /**
     * Maximum number of light-weight processes (threads) allowed.
     */
    private long lwps;

    /**
     * The version of this package.
     */
    private String version;

    /**
     * The group this package belongs to.
     */
    private String group;

    /**
     * A human-friendly description about this package.
     */
    private String description;

    /**
     * Creates a new instance. Use the fluent interface to set properties.
     */
    public Package() {
    }

    @Override
    public Map<String, Object> asMap() {
        final Map<String, Object> attributes = new LinkedHashMap<>();

        if (getId() != null) {
            attributes.put("id", getId());
        }

        if (getDescription() != null) {
            attributes.put("description", getDescription());
        }

        if (getGroup() != null) {
            attributes.put("group", getGroup());
        }

        if (getName() != null) {
            attributes.put("name", getName());
        }

        if (getVersion() != null) {
            attributes.put("version", getVersion());
        }

        attributes.put("disk", getDisk());
        attributes.put("swap", getSwap());
        attributes.put("lwps", getLwps());
        attributes.put("vcpus", getVcpus());
        attributes.put("memory", getMemory());

        return Collections.unmodifiableMap(attributes);
    }

    @Override
    public Map<String, String> asStringMap() {
        final Map<String, Object> map = asMap();

        return CloudApiUtils.asStringMap(map);
    }

    public UUID getId() {
        return id;
    }

    public Package setId(final UUID id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Package setName(final String name) {
        this.name = name;
        return this;
    }

    public long getMemory() {
        return memory;
    }

    public Package setMemory(final long memory) {
        this.memory = memory;
        return this;
    }

    public long getDisk() {
        return disk;
    }

    public Package setDisk(final long disk) {
        this.disk = disk;
        return this;
    }

    public long getSwap() {
        return swap;
    }

    public Package setSwap(final long swap) {
        this.swap = swap;
        return this;
    }

    public int getVcpus() {
        return vcpus;
    }

    public Package setVcpus(final int vcpus) {
        this.vcpus = vcpus;
        return this;
    }

    public long getLwps() {
        return lwps;
    }

    public Package setLwps(final long lwps) {
        this.lwps = lwps;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public Package setVersion(final String version) {
        this.version = version;
        return this;
    }

    public String getGroup() {
        return group;
    }

    public Package setGroup(final String group) {
        this.group = group;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Package setDescription(final String description) {
        this.description = description;
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

        final Package aPackage = (Package) o;

        return memory == aPackage.memory
                && disk == aPackage.disk
                && swap == aPackage.swap
                && vcpus == aPackage.vcpus
                && lwps == aPackage.lwps
                && Objects.equals(id, aPackage.id)
                && Objects.equals(name, aPackage.name)
                && Objects.equals(version, aPackage.version)
                && Objects.equals(group, aPackage.group)
                && Objects.equals(description, aPackage.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, memory, disk, swap, vcpus,
                lwps, version, group, description);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("name", name)
                .append("memory", memory)
                .append("disk", disk)
                .append("swap", swap)
                .append("vcpus", vcpus)
                .append("lwps", lwps)
                .append("version", version)
                .append("group", group)
                .append("description", description)
                .toString();
    }
}
