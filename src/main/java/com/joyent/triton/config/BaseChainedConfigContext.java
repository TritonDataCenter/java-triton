package com.joyent.triton.config;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * Abstract implementation of {@link ConfigContext} that allows for chaining
 * in default implementations of configuration that are delegate to when
 * we aren't passed a value.
 *
 * @author <a href="https://github.com/dekobon">Elijah Zupancic</a>
 */
public abstract class BaseChainedConfigContext implements ConfigContext {
    /**
     * CloudAPI service endpoint.
     */
    private String cloudAPIURL;

    /**
     * Account associated with the CloudAPI service.
     */
    private String account;

    /**
     * RSA key fingerprint of the private key used to access CloudAPI.
     */
    private String keyId;

    /**
     * Path on the filesystem to the private RSA key used to access CloudAPI.
     */
    private String keyPath;

    /**
     * General connection timeout for the CloudAPI service.
     */
    private Integer timeout;

    /**
     * Number of times to retry failed requests.
     */
    private Integer retries;

    /**
     * Private key content. This shouldn't be set if the KeyPath is set.
     */
    private String privateKeyContent;

    /**
     * Optional password for private key.
     */
    private String password;

    /**
     * Comma delimited list of supported TLS protocols.
     */
    private String httpsProtocols;

    /**
     * Comma delimited list of supported TLS ciphers.
     */
    private String httpsCiphers;

    /**
     * Flag indicating if HTTP signature native code generation is turned off.
     */
    private Boolean disableNativeSignatures;

    /**
     * Flag indicating if HTTP signatures are turned off.
     */
    private Boolean noAuth;

    /**
     * Time in milliseconds to cache HTTP signature headers.
     */
    private Integer signatureCacheTTL;

    /** Singleton instance of default configuration for easy reference. */
    public static final ConfigContext DEFAULT_CONFIG =
            new DefaultsConfigContext();

    /**
     * Constructor that pre-populates configuration context with the default
     * values.
     */
    public BaseChainedConfigContext() {
    }

    /**
     * Constructor that takes a default value for each one of the configuration
     * values.
     *
     * @param defaultingContext context that provides default values
     */
    public BaseChainedConfigContext(final ConfigContext defaultingContext) {
        overwriteWithContext(defaultingContext);
    }

    @Override
    public String getCloudAPIURL() {
        return this.cloudAPIURL;
    }

    @Override
    public String getUser() {
        return this.account;
    }

    @Override
    public String getKeyId() {
        return this.keyId;
    }

    @Override
    public String getKeyPath() {
        return this.keyPath;
    }

    @Override
    public Integer getTimeout() {
        return this.timeout;
    }

    @Override
    public Integer getRetries() {
        return this.retries;
    }

    @Override
    public String getPrivateKeyContent() {
        return this.privateKeyContent;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getHttpsProtocols() {
        return httpsProtocols;
    }

    @Override
    public String getHttpsCipherSuites() {
        return httpsCiphers;
    }

    @Override
    public Boolean disableNativeSignatures() {
        return disableNativeSignatures;
    }

    @Override
    public Boolean noAuth() {
        return this.noAuth;
    }

    /**
     * Overwrites the configuration values with the values of the passed context
     * if those values are not null and aren't empty.
     *
     * @param context context to overwrite configuration with
     */
    public void overwriteWithContext(final ConfigContext context) {
        if (isPresent(context.getCloudAPIURL())) {
            this.cloudAPIURL = context.getCloudAPIURL();
        }

        if (isPresent(context.getUser())) {
            this.account = context.getUser();
        }

        if (isPresent(context.getKeyId())) {
            this.keyId = context.getKeyId();
        }

        if (isPresent(context.getKeyPath())) {
            if (isPresent(context.getPrivateKeyContent())) {
                String msg = "You can't set both a private key path and private key content";
                ConfigurationException exception = new ConfigurationException(msg);
                exception.setContextValue("privateKeyPath", context.getKeyPath());
                throw exception;
            }
            this.keyPath = context.getKeyPath();
        }

        if (context.getTimeout() != null) {
            this.timeout = context.getTimeout();
        }

        if (context.getRetries() != null) {
            this.retries = context.getRetries();
        }

        if (isPresent(context.getPrivateKeyContent())) {
            if (isPresent(keyPath)) {
                String msg = "You can't set both a private key path and private key content";
                ConfigurationException exception = new ConfigurationException(msg);
                exception.setContextValue("privateKeyPath", context.getKeyPath());
                throw exception;
            }

            this.privateKeyContent = context.getPrivateKeyContent();
        }

        if (isPresent(context.getPassword())) {
            this.password = context.getPassword();
        }

        if (isPresent(context.getHttpsProtocols())) {
            this.httpsProtocols = context.getHttpsProtocols();
        }

        if (isPresent(context.getHttpsCipherSuites())) {
            this.httpsCiphers = context.getHttpsCipherSuites();
        }

        if (context.disableNativeSignatures() != null) {
            this.disableNativeSignatures = context.disableNativeSignatures();
        }

        if (context.noAuth() != null) {
            this.noAuth = context.noAuth();
        }
    }

    /**
     * Checks to see that a given string is neither empty nor null.
     * @param string string to check
     * @return true when string is non-null and not empty
     */
    protected static boolean isPresent(final String string) {
        return StringUtils.isNotBlank(string);
    }

    /**
     * Sets the CloudAPI service endpoint.
     * @param cloudAPIURL service endpoint
     * @return the current instance of {@link BaseChainedConfigContext}
     */
    public BaseChainedConfigContext setCloudAPIURL(final String cloudAPIURL) {
        this.cloudAPIURL = cloudAPIURL;
        return this;
    }

    /**
     * Sets the account associated with the CloudAPI service.
     * @param user user account
     * @return the current instance of {@link BaseChainedConfigContext}
     */
    public BaseChainedConfigContext setUser(final String user) {
        this.account = user;
        return this;
    }

    /**
     * Sets the RSA key fingerprint of the private key used to access CloudAPI.
     * @param keyId RSA key fingerprint
     * @return the current instance of {@link BaseChainedConfigContext}
     */
    public BaseChainedConfigContext setKeyId(final String keyId) {
        this.keyId = keyId;
        return this;
    }

    /**
     * Sets the path on the filesystem to the private RSA key used to access CloudAPI.
     * @param keyPath path on the filesystem
     * @return the current instance of {@link BaseChainedConfigContext}
     */
    public BaseChainedConfigContext setKeyPath(final String keyPath) {
        if (isPresent(privateKeyContent)) {
            String msg = "You can't set both a private key path and private key content";
            throw new IllegalArgumentException(msg);
        }

        this.keyPath = keyPath;
        return this;
    }

    /**
     * Sets the general connection timeout for the CloudAPI service.
     * @param timeout timeout in milliseconds
     * @return the current instance of {@link BaseChainedConfigContext}
     */
    public BaseChainedConfigContext setTimeout(final Integer timeout) {
        this.timeout = timeout;
        return this;
    }

    /**
     * Sets the number of times to retry failed HTTP requests.
     * @param retries number of times to retry
     * @return the current instance of {@link BaseChainedConfigContext}
     */
    public BaseChainedConfigContext setRetries(final Integer retries) {
        if (retries < 0) {
            ConfigurationException exception = new ConfigurationException("Retries must be zero or greater");
            exception.setContextValue("retries", retries);
            throw exception;
        }
        this.retries = retries;
        return this;
    }

    /**
     * Sets the private key content used to authenticate. This can't be set if
     * you already have a private key path specified.
     * @param privateKeyContent contents of private key in plain text
     * @return the current instance of {@link BaseChainedConfigContext}
     */
    public BaseChainedConfigContext setPrivateKeyContent(final String privateKeyContent) {
        if (isPresent(keyPath)) {
            String msg = "You can't set both a private key path and private key content";
            ConfigurationException exception = new ConfigurationException(msg);
            exception.setContextValue("keyPath", keyPath);
            throw exception;
        }

        this.privateKeyContent = privateKeyContent;

        return this;
    }

    /**
     * Sets the password used for the private key. This is optional and not
     * typically used.
     * @param password password to set
     * @return the current instance of {@link BaseChainedConfigContext}
     */
    public BaseChainedConfigContext setPassword(final String password) {
        this.password = password;

        return this;
    }

    /**
     * Set the supported TLS protocols.
     *
     * @param httpsProtocols comma delimited list of TLS protocols
     * @return the current instance of {@link BaseChainedConfigContext}
     */
    public BaseChainedConfigContext setHttpsProtocols(final String httpsProtocols) {
        this.httpsProtocols = httpsProtocols;

        return this;
    }

    /**
     * Set the supported TLS ciphers.
     *
     * @param httpsCiphers comma delimited list of TLS ciphers
     * @return the current instance of {@link BaseChainedConfigContext}
     */
    public BaseChainedConfigContext setHttpsCiphers(final String httpsCiphers) {
        this.httpsCiphers = httpsCiphers;

        return this;
    }

    /**
     * Change the state of whether or not HTTP signatures are using native code
     * to generate the cryptographic signatures.
     *
     * @param disableNativeSignatures true to disable
     * @return the current instance of {@link BaseChainedConfigContext}
     */
    public BaseChainedConfigContext setDisableNativeSignatures(final Boolean disableNativeSignatures) {
        this.disableNativeSignatures = disableNativeSignatures;

        return this;
    }

    /**
     * Change the state of whether or not HTTP signatures are sent to the Manta API.
     *
     * @param noAuth true to disable HTTP signatures
     * @return the current instance of {@link BaseChainedConfigContext}
     */
    public BaseChainedConfigContext setNoAuth(final Boolean noAuth) {
        this.noAuth = noAuth;

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

        BaseChainedConfigContext that = (BaseChainedConfigContext) o;

        return Objects.equals(cloudAPIURL, that.cloudAPIURL)
                && Objects.equals(account, that.account)
                && Objects.equals(keyId, that.keyId)
                && Objects.equals(keyPath, that.keyPath)
                && Objects.equals(timeout, that.timeout)
                && Objects.equals(retries, that.retries)
                && Objects.equals(privateKeyContent, that.privateKeyContent)
                && Objects.equals(password, that.password)
                && Objects.equals(httpsProtocols, that.httpsProtocols)
                && Objects.equals(httpsCiphers, that.httpsCiphers)
                && Objects.equals(disableNativeSignatures, that.disableNativeSignatures)
                && Objects.equals(noAuth, that.noAuth)
                && Objects.equals(signatureCacheTTL, that.signatureCacheTTL);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cloudAPIURL, account, keyId, keyPath, timeout,
                retries, privateKeyContent, password, httpsProtocols,
                httpsCiphers, disableNativeSignatures, noAuth,
                signatureCacheTTL);
    }

    /**
     * Utility method for generating to string values for all {@link ConfigContext}
     * implementations.
     *
     * @param context Context to generate String value from
     * @return string value of context
     */

    public static String stringify(final ConfigContext context) {
        final StringBuilder sb = new StringBuilder(context.getClass().getSimpleName());
        sb.append("{");
        sb.append("cloudAPIURL='").append(context.getCloudAPIURL()).append('\'');
        sb.append(", user='").append(context.getUser()).append('\'');
        sb.append(", keyId='").append(context.getKeyId()).append('\'');
        sb.append(", keyPath='").append(context.getKeyPath()).append('\'');
        sb.append(", timeout=").append(context.getTimeout());
        sb.append(", retries=").append(context.getRetries());
        sb.append(", httpsCiphers='").append(context.getHttpsCipherSuites()).append('\'');
        sb.append(", disableNativeSignatures=").append(context.disableNativeSignatures());
        sb.append(", noAuth=").append(context.noAuth());
        sb.append('}');
        return sb.toString();
    }

    @Override
    public String toString() {
        return stringify(this);
    }
}
