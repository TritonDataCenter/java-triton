package com.joyent.triton.config;

import com.joyent.triton.http.CloudApiConnectionFactory;

/**
 * Interface representing the configuration properties needed to configure a
 * {@link CloudApiConnectionFactory}.
 *
 * @author <a href="https://github.com/dekobon">Elijah Zupancic</a>
 */
public interface ConfigContext {
    /**
     * @return CloudAPI service endpoint.
     */
    String getCloudAPIURL();

    /**
     * @return account associated with the CloudAPI service.
     */
    String getUser();

    /**
     * @return RSA key fingerprint of the private key used to access CloudAPI.
     */
    String getKeyId();

    /**
     * @return Path on the filesystem to the private RSA key used to access CloudAPI.
     */
    String getKeyPath();

    /**
     * @return private key content. This can't be set if the KeyPath is set.
     */
    String getPrivateKeyContent();

    /**
     * @return password for private key. This is optional and typically not set.
     */
    String getPassword();

    /**
     * @return General connection timeout for the CloudAPI service.
     */
    Integer getTimeout();

    /**
     * @return Number of HTTP retries to perform on failure.
     */
    Integer getRetries();

    /**
     * @return a comma delimited list of HTTPS protocols
     */
    String getHttpsProtocols();

    /**
     * @return a comma delimited list of HTTPS cipher suites in order of preference
     */
    String getHttpsCipherSuites();

    /**
     * @return true when we disable using native code to generate HTTP signatures
     */
    Boolean disableNativeSignatures();

    /**
     * @return true when we disable sending HTTP signatures
     */
    Boolean noAuth();
}
