package com.joyent.triton.queryfilters;

/**
 * A {@link QueryFilter} implementation that provides a means for querying
 * specific sets of {@link Package} classes from
 * CloudAPI.
 *
 * @author <a href="https://github.com/dekobon">Elijah Zupancic</a>
 * @since 1.0.0
 */
public class PackageFilter implements QueryFilter {
    /**
     * The "friendly" name for this package.
     */
    private String name;

    /**
     * How much memory will by available (in MiB).
     */
    private Long memory;

    /**
     * How much disk space will be available (in MiB).
     */
    private Long disk;

    /**
     * How much swap space will be available (in MiB).
    */
    private Long swap;

    /**
     * Maximum number of light-weight processes (threads) allowed.
     */
    private Long lwps;

    /**
     * Number of vCPUs for this package.
     */
    private Integer vcpus;

    /**
     * The version of this package.
     */
    private String version;

    /**
     * The group this package belongs to.
     */
    private String group;

    /**
     * Creates a new instance. This class can be configured by a fluent
     * interface.
     */
    public PackageFilter() {
    }

    public String getName() {
        return name;
    }

    public PackageFilter setName(final String name) {
        this.name = name;
        return this;
    }

    public Long getMemory() {
        return memory;
    }

    public PackageFilter setMemory(final Long memory) {
        this.memory = memory;
        return this;
    }

    public Long getDisk() {
        return disk;
    }

    public PackageFilter setDisk(final Long disk) {
        this.disk = disk;
        return this;
    }

    public Long getSwap() {
        return swap;
    }

    public PackageFilter setSwap(final Long swap) {
        this.swap = swap;
        return this;
    }

    public Long getLwps() {
        return lwps;
    }

    public PackageFilter setLwps(final Long lwps) {
        this.lwps = lwps;
        return this;
    }

    public Integer getVcpus() {
        return vcpus;
    }

    public PackageFilter setVcpus(Integer vcpus) {
        this.vcpus = vcpus;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public PackageFilter setVersion(final String version) {
        this.version = version;
        return this;
    }

    public String getGroup() {
        return group;
    }

    public PackageFilter setGroup(final String group) {
        this.group = group;
        return this;
    }

    @Override
    public boolean isEmpty() {
        return getDisk() == null
                && getName() == null
                && getGroup() == null
                && getLwps() == null
                && getMemory() == null
                && getSwap() == null
                && getVcpus() == null
                && getVersion() == null;
    }
}
