package com.joyent.triton.config;

import com.joyent.triton.CloudApiUtils;
import org.apache.commons.lang3.ObjectUtils;

/**
 * An implementation of {@link ConfigContext} that reads its configuration
 * from expected environment variables.
 *
 * @author <a href="https://github.com/dekobon">Elijah Zupancic</a>
 */
public class EnvVarConfigContext implements ConfigContext {
    /**
     * Environment variable for looking up a CloudAPI URL.
     */
    public static final String TRITON_URL_ENV_KEY = "TRITON_URL";

    /**
     * Environment variable for looking up a CloudAPI URL.
     */
    public static final String SDC_URL_ENV_KEY = "SDC_URL";

    /**
     * Environment variable for looking up a CloudAPI account.
     */
    public static final String TRITON_USER_ENV_KEY = "TRITON_USER";

    /**
     * Environment variable for looking up a CloudAPI account.
     */
    public static final String SDC_USER_ENV_KEY = "SDC_USER";

    /**
     * Environment variable for looking up a CloudAPI account.
     */
    public static final String SDC_ACCOUNT_ENV_KEY = "SDC_ACCOUNT";

    /**
     * Environment variable for looking up a RSA fingerprint.
     */
    public static final String TRITON_KEY_ID_ENV_KEY = "TRITON_KEY_ID";

    /**
     * Environment variable for looking up a RSA fingerprint.
     */
    public static final String SDC_KEY_ID_ENV_KEY = "SDC_KEY_ID";

    /**
     * Environment variable for looking up a RSA private key path.
     */
    public static final String TRITON_KEY_PATH_ENV_KEY = "TRITON_KEY_PATH";

    /**
     * Environment variable for looking up a RSA private key path.
     */
    public static final String SDC_KEY_PATH_ENV_KEY = "SDC_KEY_PATH";

    /**
     * Environment variable for looking up a CloudAPI timeout.
     */
    public static final String TIMEOUT_ENV_KEY = "TRITON_TIMEOUT";

    /**
     * Environment variable for number of times to retry failed requests.
     */
    public static final String RETRIES_ENV_KEY = "TRITON_HTTP_RETRIES";

    /**
     * Environment variable for looking up CloudAPI private key content.
     */
    public static final String PRIVATE_KEY_CONTENT_ENV_KEY = "TRITON_KEY_CONTENT";

    /**
     * Environment variable for looking up CloudAPI password.
     */
    public static final String PASSWORD_ENV_KEY = "TRITON_PASSWORD";

    /**
     * Environment variable for setting TLS protocols.
     */
    public static final String HTTPS_PROTOCOLS_ENV_KEY = "TRITON_HTTPS_PROTOCOLS";

    /**
     * Environment variable for setting TLS ciphers.
     */
    public static final String HTTPS_CIPHERS_ENV_KEY = "TRITON_HTTPS_CIPHERS";

    /**
     * Environment variable for disabling native code support for generating signatures.
     */
    public static final String NO_NATIVE_SIGS_ENV_KEY = "TRITON_NO_NATIVE_SIGS";

    /**
     * Environment variable for disabling HTTP signatures.
     */
    public static final String TRITON_NO_AUTH_ENV_KEY = "TRITON_NO_AUTH";

    /**
     * Array of all environment variable names used.
     */
    public static final String[] ALL_PROPERTIES = {
            TRITON_USER_ENV_KEY, SDC_ACCOUNT_ENV_KEY, SDC_USER_ENV_KEY,
            TRITON_KEY_ID_ENV_KEY, SDC_KEY_ID_ENV_KEY,
            TRITON_KEY_PATH_ENV_KEY, SDC_KEY_PATH_ENV_KEY,
            TRITON_URL_ENV_KEY, SDC_URL_ENV_KEY,
            TIMEOUT_ENV_KEY,
            RETRIES_ENV_KEY,
            PRIVATE_KEY_CONTENT_ENV_KEY,
            PASSWORD_ENV_KEY,
            HTTPS_PROTOCOLS_ENV_KEY,
            HTTPS_CIPHERS_ENV_KEY,
            NO_NATIVE_SIGS_ENV_KEY, TRITON_NO_AUTH_ENV_KEY
    };

    /**
     * Creates a new instance that provides configuration beans via the
     * values specified in environment variables.
     */
    public EnvVarConfigContext() {
    }

    /**
     * Get the value of an environment variable where an empty string is
     * converted to null.
     *
     * @param var Environment variable name
     * @return value of environment variable
     */
    private static String getEnv(final String var) {
        return CloudApiUtils.toStringEmptyToNull(System.getenv(var));
    }

    @Override
    public String getCloudAPIURL() {
        return ObjectUtils.firstNonNull(
                getEnv(TRITON_URL_ENV_KEY),
                getEnv(SDC_URL_ENV_KEY)
        );
    }

    @Override
    public String getUser() {
        return ObjectUtils.firstNonNull(
                getEnv(TRITON_USER_ENV_KEY),
                getEnv(SDC_USER_ENV_KEY),
                getEnv(SDC_ACCOUNT_ENV_KEY)
        );
    }

    @Override
    public String getKeyId() {
        return ObjectUtils.firstNonNull(
                getEnv(TRITON_KEY_ID_ENV_KEY),
                getEnv(SDC_KEY_ID_ENV_KEY)
        );
    }

    @Override
    public String getKeyPath() {
        return ObjectUtils.firstNonNull(
                getEnv(TRITON_KEY_PATH_ENV_KEY),
                getEnv(SDC_KEY_PATH_ENV_KEY)
        );
    }

    @Override
    public String getPrivateKeyContent() {
        return getEnv(PRIVATE_KEY_CONTENT_ENV_KEY);
    }

    @Override
    public String getPassword() {
        return getEnv(PASSWORD_ENV_KEY);
    }

    @Override
    public Integer getTimeout() {
        String timeoutString = getEnv(TIMEOUT_ENV_KEY);
        return CloudApiUtils.parseIntegerOrNull(timeoutString);
    }

    @Override
    public Integer getRetries() {
        String retriesString = getEnv(RETRIES_ENV_KEY);
        return CloudApiUtils.parseIntegerOrNull(retriesString);
    }

    @Override
    public String getHttpsProtocols() {
        return getEnv(HTTPS_PROTOCOLS_ENV_KEY);
    }

    @Override
    public String getHttpsCipherSuites() {
        return getEnv(HTTPS_CIPHERS_ENV_KEY);
    }

    @Override
    public Boolean disableNativeSignatures() {
        String disableNativeString = getEnv(NO_NATIVE_SIGS_ENV_KEY);
        return CloudApiUtils.parseBooleanOrNull(disableNativeString);
    }

    @Override
    public Boolean noAuth() {
        String noAuthString = getEnv(TRITON_NO_AUTH_ENV_KEY);
        return CloudApiUtils.parseBooleanOrNull(noAuthString);
    }

    @Override
    public String toString() {
        return BaseChainedConfigContext.stringify(this);
    }
}
