package com.joyent.triton.config;

/**
 * Configuration context that is entirely driven by in-memory parameters.
 *
 * @author <a href="https://github.com/dekobon">Elijah Zupancic</a>
 */
public class StandardConfigContext extends BaseChainedConfigContext {
    /**
     * Creates a new {@link ConfigContext} implementation that allows for
     * programmatic configuration.
     */
    public StandardConfigContext() {
        super();
    }
}
