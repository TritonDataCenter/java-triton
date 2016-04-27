package com.joyent.triton.domain;

import com.joyent.triton.CloudApiUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * The error details returned from the CloudAPI REST contract are
 * consistent and predictable. This class is the representation of that error
 * state.
 *
 * @author <a href="https://github.com/dekobon">Elijah Zupancic</a>
 * @since 1.0.0
 */
public class ErrorDetail implements Entity {

    private static final long serialVersionUID = 1441751588527633252L;

    /**
     * Single word error code returned by REST API.
     */
    private String code;

    /**
     * Error message.
     */
    private String message;

    /**
     * List of error properties.
     */
    private List<Map<String, String>> errors;

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public List<Map<String, String>> getErrors() {
        return errors;
    }

    /**
     * Creates a new instance that can be fluently configured.
     */
    public ErrorDetail() {
    }

    @Override
    public Map<String, Object> asMap() {
        final Map<String, Object> attributes = new LinkedHashMap<>();

        if (getMessage() != null) {
            attributes.put("message", getMessage());
        }

        if (getCode() != null) {
            attributes.put("code", getCode());
        }

        if (getErrors() != null) {
            attributes.put("errors", getErrors());
        }

        return Collections.unmodifiableMap(attributes);
    }

    @Override
    public Map<String, String> asStringMap() {
        final Map<String, Object> map = asMap();

        return CloudApiUtils.asStringMap(map);    }

    public ErrorDetail setCode(final String code) {
        this.code = code;
        return this;
    }

    public ErrorDetail setMessage(final String message) {
        this.message = message;
        return this;
    }

    public ErrorDetail setErrors(final List<Map<String, String>> errors) {
        this.errors = errors;
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

        final ErrorDetail that = (ErrorDetail) o;

        return Objects.equals(code, that.code)
                && Objects.equals(message, that.message)
                && Objects.equals(errors, that.errors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, message, errors);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("code", code)
                .append("message", message)
                .append("errors", errors)
                .toString();
    }
}
