package com.joyent.triton.queryfilters;

import com.joyent.triton.domain.Taggable;
import com.joyent.triton.domain.Instance;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * A {@link QueryFilter} implementation that provides a means for querying
 * specific sets of {@link Instance} classes from
 * CloudAPI.
 *
 * @author <a href="https://github.com/dekobon">Elijah Zupancic</a>
 * @since 1.0.0
 */
public class InstanceFilter implements QueryFilter, Taggable<InstanceFilter> {
    /**
     * The brand of instance (zone). This could be "lx" for Linux or
     * "joyent" for SmartOS.
     */
    private String brand;

    /**
     * The "friendly" name for this machine.
     */
    private String name;

    /**
     * The image id of an instance.
     */
    private UUID image;

    /**
     * The state of the instance (e.g. running, stopped, gone).
     */
    private String state;

    /**
     * The amount of RAM this machine has (in MiB).
     */
    private Long memory;

    /**
     * Include machines destroyed in the last N minutes.
     */
    private Integer tombstone;

    /**
     * Maximum number of results to return per request.
     */
    private Integer limit;

    /**
     * The offset number to start reading results from.
     */
    private Integer offset;

    /**
     * Arbitrary set of tags that can be used as a free-form taxonomy
     * to identify an instance with extra information.
     */
    private Map<String, String> tags;

    /**
     * Whether to only list Docker instances, or only non-Docker instances, if
     * present. Defaults to showing all instances.
     */
    private Boolean listOnlyDocker;

    /**
     * Whether to include the generated credentials for machines, if present.
     * Defaults to false
     */
    private Boolean includeCredentials;

    /**
     * Creates a new instance. This class can be configured by a fluent
     * interface.
     */
    public InstanceFilter() {
    }

    public String getBrand() {
        return brand;
    }

    public InstanceFilter setBrand(final String brand) {
        this.brand = brand;
        return this;
    }

    public String getName() {
        return name;
    }

    public InstanceFilter setName(final String name) {
        this.name = name;
        return this;
    }

    public UUID getImage() {
        return image;
    }

    public InstanceFilter setImage(final UUID image) {
        this.image = image;
        return this;
    }

    public String getState() {
        return state;
    }

    public InstanceFilter setState(final String state) {
        this.state = state;
        return this;
    }

    public Long getMemory() {
        return memory;
    }

    public InstanceFilter setMemory(final Long memory) {
        this.memory = memory;
        return this;
    }

    public Integer getTombstone() {
        return tombstone;
    }

    public InstanceFilter setTombstone(final Integer tombstone) {
        this.tombstone = tombstone;
        return this;
    }

    public Integer getLimit() {
        return limit;
    }

    public InstanceFilter setLimit(final Integer limit) {
        this.limit = limit;
        return this;
    }

    public Integer getOffset() {
        return offset;
    }

    public InstanceFilter setOffset(final Integer offset) {
        this.offset = offset;
        return this;
    }

    @Override
    public Map<String, String> getTags() {
        return tags;
    }

    @Override
    public InstanceFilter setTags(final Map<String, String> tags) {
        this.tags = tags;
        return this;
    }

    public Boolean isDockerOnlyListed() {
        return listOnlyDocker;
    }

    public InstanceFilter setListOnlyDocker(final Boolean listOnlyDocker) {
        this.listOnlyDocker = listOnlyDocker;
        return this;
    }

    public Boolean isCredentialsIncluded() {
        return includeCredentials;
    }

    public InstanceFilter setIncludeCredentials(final Boolean includeCredentials) {
        this.includeCredentials = includeCredentials;
        return this;
    }

    public boolean isEmpty() {
        return getBrand() == null
                && getName() == null
                && getImage() == null
                && getState() == null
                && getMemory() == null
                && getTombstone() == null
                && getTags() == null
                && getLimit() == null
                && getOffset() == null
                && isDockerOnlyListed() == null
                && isCredentialsIncluded() == null;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final InstanceFilter that = (InstanceFilter) o;

        return Objects.equals(brand, that.brand)
                && Objects.equals(name, that.name)
                && Objects.equals(image, that.image)
                && Objects.equals(state, that.state)
                && Objects.equals(memory, that.memory)
                && Objects.equals(tombstone, that.tombstone)
                && Objects.equals(limit, that.limit)
                && Objects.equals(offset, that.offset)
                && Objects.equals(tags, that.tags)
                && Objects.equals(listOnlyDocker, that.listOnlyDocker)
                && Objects.equals(includeCredentials, that.includeCredentials);
    }

    @Override
    public int hashCode() {
        return Objects.hash(brand, name, image, state, memory, tombstone, limit,
                offset, tags, listOnlyDocker, includeCredentials);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("InstanceFilter{");
        sb.append("brand='").append(brand).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", image=").append(image);
        sb.append(", state='").append(state).append('\'');
        sb.append(", memory=").append(memory);
        sb.append(", tombstone=").append(tombstone);
        sb.append(", limit=").append(limit);
        sb.append(", offset=").append(offset);
        sb.append(", tags=").append(tags);
        sb.append(", listOnlyDocker=").append(listOnlyDocker);
        sb.append(", includeCredentials=").append(includeCredentials);
        sb.append('}');
        return sb.toString();
    }
}
