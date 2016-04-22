package com.joyent.triton.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.threetenbp.ThreeTenModule;

/**
 * Customized and configured instance of {@link ObjectMapper}.
 *
 * @author <a href="https://github.com/dekobon">Elijah Zupancic</a>
 * @since 1.0.0
 */
public class CloudApiObjectMapper extends ObjectMapper {

    private static final long serialVersionUID = -1929306315957650379L;

    {
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        setSerializationInclusion(JsonInclude.Include.NON_NULL);
        registerModule(new ThreeTenModule());
        registerModule(new PostprocessModificationModule());
    }

    /**
     * Creates an {@link ObjectMapper} instance configured for the needs of
     * the SDK.
     */
    public CloudApiObjectMapper() {
    }

    /**
     * Creates an {@link ObjectMapper} instance configured for the needs of
     * the SDK with the option for pretty printing JSON output.
     *
     * @param prettyPrint when true pretty print JSON output
     */
    public CloudApiObjectMapper(final boolean prettyPrint) {
        if (prettyPrint) {
            configure(SerializationFeature.INDENT_OUTPUT, true);
        }
    }
}
