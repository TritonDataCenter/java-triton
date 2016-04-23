package com.joyent.triton.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * The error details returned from the CloudAPI API's REST contract are
 * consistent and predictable. This class is the representation of that error
 * state.
 *
 * @author <a href="https://github.com/dekobon">Elijah Zupancic</a>
 * @since 1.0.0
 */
public class ErrorDetail {
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
