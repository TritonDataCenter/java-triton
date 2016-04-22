package com.joyent.triton.config;

import org.apache.commons.lang3.SystemUtils;

import java.io.File;

/**
 * {@link ConfigContext} implementation that outputs nothing but the default
 * values for all of the configuration settings.
 *
 * @author <a href="https://github.com/dekobon">Elijah Zupancic</a>
 */
public class DefaultsConfigContext implements ConfigContext {
    /**
     * The default CloudAPI service endpoint - a public cloud endpoint.
     */
    public static final String DEFAULT_CLOUDAPI_URL = "https://us-east-1.api.joyent.com:443";

    /**
     * The default timeout for accessing the service.
     */
    public static final int DEFAULT_HTTP_TIMEOUT = 20 * 1000;

    /**
     * The default number of times to retry failed requests.
     */
    public static final int DEFAULT_HTTP_RETRIES = 3;

    /**
     * We assume the default rsa key in the user's home directory.
     */
    public static final String DEFAULT_KEY_PATH;

    /**
     * Default TLS protocols.
     */
    public static final String DEFAULT_HTTPS_PROTOCOLS = "TLSv1.2,TLSv1.1,TLSv1";

    /**
     * Default TLS cipher suites.
     */
    public static final String DEFAULT_HTTPS_CIPHERS;

    static {
        // Don't even bother setting a default key path if it doesn't exist
        String defaultKeyPath = String.format("%s/.ssh/id_rsa",
                SystemUtils.USER_HOME);
        File privateKeyFile = new File(defaultKeyPath);

        if (privateKeyFile.exists() && privateKeyFile.canRead()) {
            DEFAULT_KEY_PATH = defaultKeyPath;
        } else {
            DEFAULT_KEY_PATH = null;
        }

        // Not all of the desired ciphers are supported in version pre 1.8
        if (SystemUtils.IS_JAVA_1_8 || SystemUtils.IS_JAVA_1_9) {
            DEFAULT_HTTPS_CIPHERS =
                    "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA,"
                    + "TLS_RSA_WITH_AES_256_CBC_SHA256,"
                    + "TLS_RSA_WITH_AES_128_CBC_SHA256,"
                    + "TLS_RSA_WITH_AES_128_CBC_SHA";
        } else {
            DEFAULT_HTTPS_CIPHERS =
                    "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA,"
                    + "TLS_RSA_WITH_AES_128_CBC_SHA256,"
                    + "TLS_RSA_WITH_AES_128_CBC_SHA";
        }
    }

    /**
     * Creates a new instance with all of the defaults assigned to the beans
     * defined in {@link ConfigContext}.
     */
    public DefaultsConfigContext() {
    }

    @Override
    public String getCloudAPIURL() {
        return DEFAULT_CLOUDAPI_URL;
    }

    @Override
    public String getUser() {
        return null;
    }

    @Override
    public String getKeyId() {
        return null;
    }

    @Override
    public String getKeyPath() {
        return DEFAULT_KEY_PATH;
    }

    @Override
    public String getPrivateKeyContent() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public Integer getTimeout() {
        return DEFAULT_HTTP_TIMEOUT;
    }

    @Override
    public Integer getRetries() {
        return DEFAULT_HTTP_RETRIES;
    }

    @Override
    public String getHttpsProtocols() {
        return DEFAULT_HTTPS_PROTOCOLS;
    }

    @Override
    public String getHttpsCipherSuites() {
        return DEFAULT_HTTPS_CIPHERS;
    }

    @Override
    public Boolean disableNativeSignatures() {
        return false;
    }

    @Override
    public Boolean noAuth() {
        return false;
    }

    @Override
    public String toString() {
        return BaseChainedConfigContext.stringify(this);
    }
}
