package com.joyent.triton.config;

import com.joyent.triton.CloudApiUtils;

import java.util.Map;

/**
 * {@link ConfigContext} implementation that is used for configuring instances
 * from a Map.
 *
 * @author <a href="https://github.com/dekobon">Elijah Zupancic</a>
 */
public class MapConfigContext implements ConfigContext {
    /**
     * Property key for looking up a CloudAPI URL.
     */
    public static final String URL_KEY = "triton.url";

    /**
     * Property key for looking up a CloudAPI account.
     */
    public static final String USER_KEY = "triton.user";

    /**
     * Property key for looking up a RSA fingerprint.
     */
    public static final String KEY_ID_KEY = "triton.key_id";

    /**
     * Property key for looking up a RSA private key path.
     */
    public static final String KEY_PATH_KEY = "triton.key_path";

    /**
     * Property key for looking up a CloudAPI timeout.
     */
    public static final String TIMEOUT_KEY = "triton.timeout";

    /**
     * Property key for number of times to retry failed requests.
     */
    public static final String RETRIES_KEY = "triton.retries";

    /**
     * Property key for the maximum number of open connections to the CloudAPI API.
     */
    public static final String MAX_CONNS_KEY = "triton.max_connections";

    /**
     * Property key for looking up CloudAPI private key content.
     */
    public static final String PRIVATE_KEY_CONTENT_KEY = "triton.key_content";

    /**
     * Property key for looking up CloudAPI password.
     */
    public static final String PASSWORD_KEY = "triton.password";

    /**
     * Property key for setting TLS protocols.
     */
    public static final String HTTPS_PROTOCOLS_KEY = "https.protocols";

    /**
     * Property key for setting TLS ciphers.
     */
    public static final String HTTPS_CIPHERS_KEY = "https.cipherSuites";

    /**
     * Property key for disabling HTTP signatures.
     */
    public static final String NO_AUTH_KEY = "triton.no_auth";

    /**
     * Property key for disabling native code support for generating signatures.
     */
    public static final String NO_NATIVE_SIGS_KEY = "triton.disable_native_sigs";

    // I know manually adding them all sucks, but it is the simplest operation
    // for a shared library. We could do all sorts of complicated reflection
    // or annotation processing, but they are error-prone.
    /**
     * List of all properties that we read from configuration.
     */
    public static final String[] ALL_PROPERTIES = {
            URL_KEY, USER_KEY, KEY_ID_KEY,
            KEY_PATH_KEY, TIMEOUT_KEY, RETRIES_KEY,
            MAX_CONNS_KEY, PRIVATE_KEY_CONTENT_KEY,
            PASSWORD_KEY,
            EnvVarConfigContext.HTTPS_PROTOCOLS_ENV_KEY, HTTPS_CIPHERS_KEY,
            NO_AUTH_KEY, NO_NATIVE_SIGS_KEY
    };

    /**
     * Internal map used as the source of the configuration bean values.
     */
    private final Map<?, ?> backingMap;

    /**
     * Creates a new instance using the passed {@link Map} implementation as
     * a backing store.
     *
     * @param backingMap Map implementation used for the values of the configuration beans
     */
    public MapConfigContext(final Map<?, ?> backingMap) {
        this.backingMap = backingMap;
    }

    @Override
    public String getCloudAPIURL() {
        return normalizeEmptyAndNullAndDefaultToStringValue(
                URL_KEY, EnvVarConfigContext.TRITON_URL_ENV_KEY);
    }

    @Override
    public String getUser() {
        return normalizeEmptyAndNullAndDefaultToStringValue(
                USER_KEY, EnvVarConfigContext.TRITON_USER_ENV_KEY);
    }

    @Override
    public String getKeyId() {
        return normalizeEmptyAndNullAndDefaultToStringValue(
                KEY_ID_KEY, EnvVarConfigContext.TRITON_KEY_ID_ENV_KEY);
    }

    @Override
    public String getKeyPath() {
        return normalizeEmptyAndNullAndDefaultToStringValue(
                KEY_PATH_KEY, EnvVarConfigContext.TRITON_KEY_PATH_ENV_KEY);
    }

    @Override
    public String getPrivateKeyContent() {
        return normalizeEmptyAndNullAndDefaultToStringValue(
                PRIVATE_KEY_CONTENT_KEY, EnvVarConfigContext.PRIVATE_KEY_CONTENT_ENV_KEY);
    }

    @Override
    public String getPassword() {
        return normalizeEmptyAndNullAndDefaultToStringValue(
                PASSWORD_KEY, EnvVarConfigContext.PASSWORD_ENV_KEY);
    }

    @Override
    public Integer getTimeout() {
        Integer mapValue = CloudApiUtils.parseIntegerOrNull(backingMap.get(TIMEOUT_KEY));

        if (mapValue != null) {
            return mapValue;
        }

        return CloudApiUtils.parseIntegerOrNull(backingMap.get(EnvVarConfigContext.TIMEOUT_ENV_KEY));
    }

    @Override
    public Integer getRetries() {
        Integer mapValue = CloudApiUtils.parseIntegerOrNull(backingMap.get(RETRIES_KEY));

        if (mapValue != null) {
            return mapValue;
        }

        return CloudApiUtils.parseIntegerOrNull(backingMap.get(EnvVarConfigContext.RETRIES_ENV_KEY));
    }

    @Override
    public String getHttpsProtocols() {
        return normalizeEmptyAndNullAndDefaultToStringValue(
                HTTPS_PROTOCOLS_KEY, EnvVarConfigContext.HTTPS_PROTOCOLS_ENV_KEY);
    }

    @Override
    public String getHttpsCipherSuites() {
        return normalizeEmptyAndNullAndDefaultToStringValue(
                HTTPS_CIPHERS_KEY, EnvVarConfigContext.HTTPS_CIPHERS_ENV_KEY);
    }

    @Override
    public Boolean disableNativeSignatures() {
        Boolean mapValue = CloudApiUtils.parseBooleanOrNull(backingMap.get(NO_NATIVE_SIGS_KEY));

        if (mapValue != null) {
            return mapValue;
        }

        return CloudApiUtils.parseBooleanOrNull(backingMap.get(EnvVarConfigContext.NO_NATIVE_SIGS_ENV_KEY));
    }

    @Override
    public Boolean noAuth() {
        Boolean mapValue = CloudApiUtils.parseBooleanOrNull(backingMap.get(NO_AUTH_KEY));

        if (mapValue != null) {
            return mapValue;
        }

        return CloudApiUtils.parseBooleanOrNull(backingMap.get(EnvVarConfigContext.TRITON_NO_AUTH_ENV_KEY));
    }

    /**
     * Allows the caller to perform a put operation on the backing map of the
     * context. This is typically used by other {@link ConfigContext}
     * implementations that need to cobble together multiple map values.
     *
     * This method is scoped to default because no other packages should be
     * using it.
     *
     * @param key configuration key
     * @param value configuration value
     * @return return value of the put() operation from the backing map
     */
    Object put(final String key, final String value) {
        if (key == null) {
            throw new IllegalArgumentException("Config key can't be null");
        }

        if (key.isEmpty()) {
            throw new IllegalArgumentException("Config key can't be blank");
        }

        // Java generics can be stupid
        @SuppressWarnings("unchecked")
        Map<Object, Object> map = (Map<Object, Object>)this.backingMap;
        return map.put(key, value);
    }

    /**
     * Normalizes a value pulled from the backingMap.
     * @param keys key to use to pull value from backing map
     * @return null on empty string or null, otherwise value from backing map
     */
    private String normalizeEmptyAndNullAndDefaultToStringValue(final Object... keys) {
        for (Object k : keys) {
            String value = CloudApiUtils.toStringEmptyToNull(backingMap.get(k));
            if (value == null) {
                continue;
            }

            return value;
        }

        // We couldn't find any values
        return null;
    }

    @Override
    public String toString() {
        return BaseChainedConfigContext.stringify(this);
    }
}
