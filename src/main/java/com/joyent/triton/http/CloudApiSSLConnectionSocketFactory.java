package com.joyent.triton.http;

import com.joyent.triton.CloudApiUtils;
import com.joyent.triton.config.ConfigContext;
import com.joyent.triton.config.ConfigurationException;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.ssl.SSLContexts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Custom {@link SSLConnectionSocketFactory} implementation that consumes CloudAPI
 * configuration and enforces the selection of protocols and ciphers.
 *
 * @author <a href="https://github.com/dekobon">Elijah Zupancic</a>
 */
public class CloudApiSSLConnectionSocketFactory extends SSLConnectionSocketFactory {
    /**
     * Logger instance.
     */
    private static final Logger LOG = LoggerFactory.getLogger(CloudApiSSLConnectionSocketFactory.class);

    /**
     * Set of supported TLS protocols.
     */
    private final Set<String> supportedProtocols;

    /**
     * Set of supported TLS cipher suites.
     */
    private final Set<String> supportedCipherSuites;

    /**
     * Creates a new instance using the configuration parameters.
     * @param config configuration context containing SSL config params
     */
    public CloudApiSSLConnectionSocketFactory(final ConfigContext config) {
        super(buildContext(),
              CloudApiUtils.csv2array(config.getHttpsProtocols()),
              CloudApiUtils.csv2array(config.getHttpsCipherSuites()),
              getDefaultHostnameVerifier());

        if (config.getHttpsProtocols() != null) {
            this.supportedProtocols = new LinkedHashSet<>(CloudApiUtils.fromCsv(config.getHttpsProtocols()));
        } else {
            this.supportedProtocols = Collections.emptySet();
        }

        if (config.getHttpsCipherSuites() != null) {
            this.supportedCipherSuites = new LinkedHashSet<>(CloudApiUtils.fromCsv(config.getHttpsCipherSuites()));
        } else {
            this.supportedCipherSuites = Collections.emptySet();
        }
    }

    /**
     * @return reference to SSL Context
     */
    private static SSLContext buildContext() {
        return SSLContexts.createDefault();
    }

    @Override
    protected void prepareSocket(final SSLSocket socket) throws IOException {
        final Set<String> enabledProtocols = new LinkedHashSet<>(
                Arrays.asList(socket.getEnabledProtocols()));
        final Set<String> enabledCipherSuites = new LinkedHashSet<>(
                Arrays.asList(socket.getEnabledCipherSuites()));

        if (LOG.isDebugEnabled()) {
            LOG.debug("Enabled TLS protocols: {}", CloudApiUtils.asString(enabledProtocols));
            LOG.debug("Enabled cipher suites: {}", CloudApiUtils.asString(enabledCipherSuites));
        }

        supportedCipherSuites.retainAll(enabledCipherSuites);

        if (!supportedCipherSuites.isEmpty()) {
            try {
                String[] supportedCiphers = new String[supportedCipherSuites.size()];
                supportedCipherSuites.toArray(supportedCiphers);
                socket.setEnabledCipherSuites(supportedCiphers);
            } catch (IllegalArgumentException e) {
                String msg = String.format("Unsupported encryption provider. Supported providers: %s",
                        CloudApiUtils.asString(socket.getEnabledCipherSuites()));
                throw new ConfigurationException(msg, e);
            }
        }

        supportedProtocols.retainAll(enabledProtocols);

        if (!supportedProtocols.isEmpty()) {
            String[] supportedProtos = new String[supportedProtocols.size()];
            supportedProtocols.toArray(supportedProtos);
            socket.setEnabledProtocols(supportedProtos);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Supported TLS protocols: {}", CloudApiUtils.asString(supportedProtocols));
            LOG.debug("Supported cipher suites: {}", CloudApiUtils.asString(supportedCipherSuites));
        }
    }
}
