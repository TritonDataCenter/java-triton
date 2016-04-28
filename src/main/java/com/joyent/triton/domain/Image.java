package com.joyent.triton.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.joyent.triton.CloudApiUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.threeten.bp.Instant;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Domain object representing the an "image" in CloudAPI. Images are operating system
 * templates that define a base operating system platform. Images can be copied from
 * existing running instances. Images can be made from LX brand zones, SmartOS zones
 * or KVM instances.
 *
 * @author <a href="https://github.com/dekobon">Elijah Zupancic</a>
 * @since 1.0.0
 */
public class Image implements Entity {

    private static final long serialVersionUID = -6044842321633324058L;

    /**
     * Unique id for this image.
     */
    private UUID id;

    /**
     * The "friendly" name for this image.
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
     * What kind of image this is.
     */
    private String type;

    /**
     * Contains a grouping of various minimum requirements for provisioning a machine with this image.
     * For example 'password' indicates that a password must be provided.
     */
    private Map<String, String> requirements;

    /**
     * The URL for a web page with more detailed information for this image.
     */
    private String homepage;

    /**
     * Plain text describing the image.
     */
    private String description;

    /**
     * An array of image files that make up each image. Currently only a single file per image is supported.
     */
    private Set<ImageFiles> files;

    /**
     * The time this image has been made publicly available.
     */
    @JsonProperty("published_at")
    private Instant publishedAt;

    /**
     * The UUID of the user who owns this image.
     */
    private UUID owner;

    /**
     * Indicates if this image is publicly available.
     */
    @JsonProperty("public")
    private boolean publiclyAvailable;

    /**
     * The current state of the image. One of 'active', 'unactivated', 'disabled', 'creating', 'failed'.
     */
    private String state;

    /**
     * An object of key/value pairs that allows clients to categorize images by any given criteria.
     */
    private Map<String, String> tags;

    /**
     * URL of the End User License Agreement (EULA) for the image.
     */
    private String eula;

    /**
     * Access Control List. An array of account UUIDs given access to a private image.
     * The field is only relevant to private images.
     */
    private Set<UUID> acl;

    /**
     * If <code>state=="failed"</code>, resulting from CreateImageFromMachine failure, then there
     * may be an error object of the form:
     * <code>{"code": "<string error code>", "message": "<string desc>"}</code>.
     */
    private List<ErrorDetail> errors;

    @Override
    public Map<String, Object> asMap() {
        final Map<String, Object> attributes = new LinkedHashMap<>();

        if (getId() != null) {
            attributes.put("id", getId());
        }

        if (getName() != null) {
            attributes.put("name", getName());
        }

        if (getVersion() != null) {
            attributes.put("version", getVersion());
        }

        if (getOs() != null) {
            attributes.put("os", getOs());
        }

        if (getRequirements() != null) {
            attributes.put("requirements", getRequirements());
        }

        if (getType() != null) {
            attributes.put("type", getType());
        }

        if (getDescription() != null) {
            attributes.put("description", getDescription());
        }

        if (getFiles() != null) {
            attributes.put("files", getFiles());
        }

        if (getTags() != null) {
            attributes.put("tags", getTags());
        }

        if (getHomepage() != null) {
            attributes.put("homepage", getHomepage());
        }

        if (getPublishedAt() != null) {
            attributes.put("published_at", getPublishedAt());
        }

        if (getOwner() != null) {
            attributes.put("owner", getOwner());
        }

        if (getState() != null) {
            attributes.put("state", getState());
        }

        attributes.put("public", isPubliclyAvailable());

        return Collections.unmodifiableMap(attributes);
    }

    @Override
    public Map<String, String> asStringMap() {
        final Map<String, Object> map = asMap();

        return CloudApiUtils.asStringMap(map);    }

    public List<ErrorDetail> getErrors() {
        return errors;
    }

    public Image setErrors(final List<ErrorDetail> errors) {
        this.errors = errors;
        return this;
    }

    public Set<UUID> getAcl() {
        return acl;
    }

    public Image setAcl(final Set<UUID> acl) {
        this.acl = acl;
        return this;
    }

    public String getEula() {
        return eula;
    }

    public Image setEula(final String eula) {
        this.eula = eula;
        return this;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public Image setTags(final Map<String, String> tags) {
        this.tags = tags;
        return this;
    }

    public String getState() {
        return state;
    }

    public Image setState(final String state) {
        this.state = state;
        return this;
    }

    public boolean isPubliclyAvailable() {
        return publiclyAvailable;
    }

    public Image setPubliclyAvailable(final boolean publiclyAvailable) {
        this.publiclyAvailable = publiclyAvailable;
        return this;
    }

    public UUID getOwner() {
        return owner;
    }

    public Image setOwner(final UUID owner) {
        this.owner = owner;
        return this;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }

    public Image setPublishedAt(final Instant publishedAt) {
        this.publishedAt = publishedAt;
        return this;
    }

    public Set<ImageFiles> getFiles() {
        return files;
    }

    public Image setFiles(final Set<ImageFiles> files) {
        this.files = files;
        return this;
    }

    public String getHomepage() {
        return homepage;
    }

    public Image setHomepage(final String homepage) {
        this.homepage = homepage;
        return this;
    }

    public Map<String, String> getRequirements() {
        return requirements;
    }

    public Image setRequirements(final Map<String, String> requirements) {
        this.requirements = requirements;
        return this;
    }

    public String getType() {
        return type;
    }

    public Image setType(final String type) {
        this.type = type;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public Image setVersion(final String version) {
        this.version = version;
        return this;
    }

    public String getOs() {
        return os;
    }

    public Image setOs(final String os) {
        this.os = os;
        return this;
    }

    public String getName() {
        return name;
    }

    public Image setName(final String name) {
        this.name = name;
        return this;
    }

    public UUID getId() {
        return id;
    }

    public Image setId(final UUID id) {
        this.id = id;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Image setDescription(final String description) {
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
        Image image = (Image) o;
        return publiclyAvailable == image.publiclyAvailable
                && Objects.equals(id, image.id)
                && Objects.equals(name, image.name)
                && Objects.equals(os, image.os)
                && Objects.equals(version, image.version)
                && Objects.equals(type, image.type)
                && Objects.equals(requirements, image.requirements)
                && Objects.equals(description, image.description)
                && Objects.equals(homepage, image.homepage)
                && Objects.equals(files, image.files)
                && Objects.equals(publishedAt, image.publishedAt)
                && Objects.equals(owner, image.owner)
                && Objects.equals(state, image.state)
                && Objects.equals(tags, image.tags)
                && Objects.equals(eula, image.eula)
                && Objects.equals(acl, image.acl)
                && Objects.equals(errors, image.errors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, os, version, type, requirements, description,
                homepage, files, publishedAt, owner, publiclyAvailable,
                state, tags, eula, acl, errors);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("name", name)
                .append("os", os)
                .append("version", version)
                .append("type", type)
                .append("requirements", requirements)
                .append("description", description)
                .append("homepage", homepage)
                .append("files", CloudApiUtils.csv(files))
                .append("publishedAt", publishedAt)
                .append("owner", owner)
                .append("publiclyAvailable", publiclyAvailable)
                .append("state", state)
                .append("tags", tags)
                .append("eula", eula)
                .append("acl", acl)
                .append("errors", CloudApiUtils.csv(errors))
                .toString();
    }
}
