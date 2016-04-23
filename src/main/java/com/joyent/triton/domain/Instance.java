package com.joyent.triton.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.joyent.triton.CloudApiUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.threeten.bp.Instant;

import java.net.InetAddress;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Domain object representing an instance of compute. The CloudAPI uses the term
 * machine, but we are using the latest domain vocabulary in order to be
 * consistent with future documentation.
 *
 * @author <a href="https://github.com/dekobon">Elijah Zupancic</a>
 * @since 1.0.0
 */
public class Instance implements Entity, Taggable<Instance>, MetadataEnabled<Instance> {
    /**
     * Java object serialization id.
     */
    private static final long serialVersionUID = 2328210274606647142L;

    /**
     * Unique id for this instance.
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private UUID id;

    /**
     * The "friendly" name for this machine.
     */
    private String name;

    /**
     * The brand of instance (zone). This could be "lx" for Linux or
     * "joyent" for SmartOS.
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String brand;

    /**
     * The state of the instance (e.g. running, stopped, gone).
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String state;

    /**
     * The image id this instance was provisioned with.
     */
    private UUID image;

    /**
     * The IP addresses this instance has.
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Set<InetAddress> ips;

    /**
     * The amount of RAM this instance has (in MiB).
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private long memory;

    /**
     * The amount of disk this instance has (in MiB).
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private long disk;

    /**
     * Any additional metadata this instance has.
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Map<String, String> metadata;

    /**
     * Arbitrary set of tags that can be used as a free-form taxonomy
     * to identify an instance with extra information that can be acted upon
     * by external utilities like firewall as a service.
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Map<String, String> tags;

    /**
     * When this instance was created.
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Instant created;

    /**
     * When this instance's details was last updated.
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Instant updated;

    /**
     * The network UUIDs of the network interfaces this instance has.
     */
    private Set<UUID> networks;

    /**
     * IP address of the primary network interface of this instance.
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private InetAddress primaryIp;

    /**
     * Flag indicating that the firewall is enabled for this instance.
     */
    @JsonProperty("firewall_enabled")
    private boolean firewallEnabled;

    /**
     * UUID of the server on which the instance is located.
     */
    @JsonProperty(value = "compute_node", access = JsonProperty.Access.WRITE_ONLY)
    private UUID computeNode;

    /**
     * The name of the package used to create this instance.
     */
    @JsonProperty(value = "package", access = JsonProperty.Access.WRITE_ONLY)
    private String packageName;

    /**
     * DNS names of the instance.
     */
    @JsonProperty(value = "dns_names", access = JsonProperty.Access.WRITE_ONLY)
    private Set<String> dnsNames;

    // --- Used in serialization only

    /**
     * Optionally specify which instances the new instances should be near or far from.
     */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Locality locality;

    /**
     * UUID Id of the package to use on provisioning.
     */
    @JsonProperty(value = "package", access = JsonProperty.Access.READ_ONLY)
    private UUID packageId;

    /**
     * Creates a new instance. Use the fluent interface to set properties.
     */
    public Instance() {
    }

    /**
     * Derives a set of only the private IPs from the instance's IPs.
     * @return immutable set of private IP addresses
     */
    public Set<InetAddress> privateIPs() {
        if (getIps() == null) {
            return null;
        }

        return CloudApiUtils.findUniquePrivateIps(getIps());
    }

    @Override
    public Map<String, Object> asMap() {
        final Map<String, Object> attributes = new LinkedHashMap<>();

        if (getBrand() != null) {
            attributes.put("brand", getBrand());
        }

        if (getComputeNode() != null) {
            attributes.put("compute_node", getComputeNode());
        }

        if (getCreated() != null) {
            attributes.put("created", getCreated());
        }

        if (getDnsNames() != null) {
            attributes.put("dns_names", getDnsNames());
        }

        if (getId() != null) {
            attributes.put("id", getId());
        }

        if (getImage() != null) {
            attributes.put("image", getImage());
        }

        if (getIps() != null) {
            attributes.put("ips", getIps());
        }

        if (getLocality() != null) {
            attributes.put("locality", getLocality());
        }

        if (getMetadata() != null) {
            attributes.put("metadata", getMetadata());
        }

        if (getName() != null) {
            attributes.put("name", getName());
        }

        if (getNetworks() != null) {
            attributes.put("networks", getNetworks());
        }

        if (getPackageId() != null) {
            attributes.put("package_id", getPackageId());
        }

        if (getPackageName() != null) {
            attributes.put("package_name", getPackageName());
        }

        if (getPrimaryIp() != null) {
            attributes.put("primary_ip", getPrimaryIp());
        }

        if (getState() != null) {
            attributes.put("state", getState());
        }

        if (getTags() != null) {
            attributes.put("tags", getTags());
        }

        if (getUpdated() != null) {
            attributes.put("updated", getUpdated());
        }

        attributes.put("disk", getDisk());
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

    Instance setId(final UUID id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Instance setName(final String name) {
        this.name = name;
        return this;
    }

    public String getBrand() {
        return brand;
    }

    Instance setBrand(final String brand) {
        this.brand = brand;
        return this;
    }

    public String getState() {
        return state;
    }

    Instance setState(final String state) {
        this.state = state;
        return this;
    }

    public UUID getImage() {
        return image;
    }

    public Instance setImage(final UUID image) {
        this.image = image;
        return this;
    }

    public Set<InetAddress> getIps() {
        return ips;
    }

    Instance setIps(final Set<InetAddress> ips) {
        this.ips = ips;
        return this;
    }

    public long getMemory() {
        return memory;
    }

    Instance setMemory(final long memory) {
        this.memory = memory;
        return this;
    }

    public long getDisk() {
        return disk;
    }

    Instance setDisk(final long disk) {
        this.disk = disk;
        return this;
    }

    @Override
    public Map<String, String> getMetadata() {
        return metadata;
    }

    @Override
    public Instance setMetadata(final Map<String, String> metadata) {
        this.metadata = metadata;
        return this;
    }

    @Override
    public Map<String, String> getTags() {
        return tags;
    }

    @Override
    public Instance setTags(final Map<String, String> tags) {
        this.tags = tags;
        return this;
    }

    public Instant getCreated() {
        return created;
    }

    Instance setCreated(final Instant created) {
        this.created = created;
        return this;
    }

    public Instant getUpdated() {
        return updated;
    }

    Instance setUpdated(final Instant updated) {
        this.updated = updated;
        return this;
    }

    public Set<UUID> getNetworks() {
        return networks;
    }

    public Instance setNetworks(final Set<UUID> networks) {
        this.networks = networks;
        return this;
    }

    public InetAddress getPrimaryIp() {
        return primaryIp;
    }

    Instance setPrimaryIp(final InetAddress primaryIp) {
        this.primaryIp = primaryIp;
        return this;
    }

    public boolean isFirewallEnabled() {
        return firewallEnabled;
    }

    public Instance setFirewallEnabled(final boolean firewallEnabled) {
        this.firewallEnabled = firewallEnabled;
        return this;
    }

    public UUID getComputeNode() {
        return computeNode;
    }

    Instance setComputeNode(final UUID computeNode) {
        this.computeNode = computeNode;
        return this;
    }

    public String getPackageName() {
        return packageName;
    }

    Instance setPackageName(final String packageName) {
        this.packageName = packageName;
        return this;
    }

    public UUID getPackageId() {
        return packageId;
    }

    public Instance setPackageId(final UUID packageId) {
        this.packageId = packageId;
        return this;
    }

    public Set<String> getDnsNames() {
        return dnsNames;
    }

    Instance setDnsNames(final Set<String> dnsNames) {
        this.dnsNames = dnsNames;
        return this;
    }

    public Locality getLocality() {
        return locality;
    }

    public Instance setLocality(final Locality locality) {
        this.locality = locality;
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

        final Instance instance = (Instance) o;

        return memory == instance.memory
                && disk == instance.disk
                && firewallEnabled == instance.firewallEnabled
                && Objects.equals(id, instance.id)
                && Objects.equals(name, instance.name)
                && Objects.equals(brand, instance.brand)
                && Objects.equals(state, instance.state)
                && Objects.equals(image, instance.image)
                && Objects.equals(ips, instance.ips)
                && Objects.equals(metadata, instance.metadata)
                && Objects.equals(tags, instance.tags)
                && Objects.equals(created, instance.created)
                && Objects.equals(updated, instance.updated)
                && Objects.equals(networks, instance.networks)
                && Objects.equals(primaryIp, instance.primaryIp)
                && Objects.equals(computeNode, instance.computeNode)
                && Objects.equals(packageName, instance.packageName)
                && Objects.equals(dnsNames, instance.dnsNames)
                && Objects.equals(locality, instance.locality);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, brand, state, image, ips, memory,
                disk, metadata, tags, created, updated, networks, primaryIp,
                firewallEnabled, computeNode, packageName, dnsNames, locality);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("name", name)
                .append("brand", brand)
                .append("state", state)
                .append("image", image)
                .append("ips", CloudApiUtils.asString(ips))
                .append("memory", memory)
                .append("disk", disk)
                .append("metadata", CloudApiUtils.asString(metadata))
                .append("tags", CloudApiUtils.asString(tags))
                .append("created", created)
                .append("updated", updated)
                .append("networks", networks)
                .append("primaryIp", primaryIp)
                .append("firewallEnabled", firewallEnabled)
                .append("computeNode", computeNode)
                .append("packageName", packageName)
                .append("dnsNames", dnsNames)
                .append("locality", locality)
                .append("packageId", packageId)
                .toString();
    }
}
