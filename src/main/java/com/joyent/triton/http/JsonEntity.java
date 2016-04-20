package com.joyent.triton.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Implementation of {@link HttpEntity} that specifically uses Jackson to
 * serialize content bodies (entities).
 */
@SuppressWarnings("deprecation")
public class JsonEntity implements HttpEntity {
    /**
     * Backing Jackson object mapper implementation.
     */
    private final ObjectMapper mapper;

    /**
     * Flag indicating that this entity can be redone.
     */
    private final boolean repeatable;

    /**
     * The source object that is serialized from.
     */
    private final Object sourceObject;

    /**
     * Creates a new instance.
     *
     * @param mapper backing Jackson object mapper implementation
     * @param repeatable flag indicating that this entity can be redone
     * @param sourceObject the source object that is serialized from
     */
    public JsonEntity(final ObjectMapper mapper,
                      final boolean repeatable,
                      final Object sourceObject) {
        this.mapper = mapper;
        this.repeatable = repeatable;
        this.sourceObject = sourceObject;
    }

    /**
     * Creates a new instance.
     *
     * @param mapper backing Jackson object mapper implementation
     * @param sourceObject the source object that is serialized from
     */
    public JsonEntity(final ObjectMapper mapper,
                      final Object sourceObject) {
        this(mapper, true, sourceObject);
    }

    @Override
    public boolean isRepeatable() {
        return repeatable;
    }

    @Override
    public boolean isChunked() {
        return true;
    }

    @Override
    public long getContentLength() {
        return -1;
    }

    @Override
    public Header getContentType() {
        return new BasicHeader(
                HTTP.CONTENT_TYPE,
                ContentType.APPLICATION_JSON.toString()
        );
    }

    @Override
    public Header getContentEncoding() {
        return null;
    }

    @Override
    public InputStream getContent() throws IOException, UnsupportedOperationException {
        final byte[] json = mapper.writeValueAsBytes(sourceObject);
        return new ByteArrayInputStream(json);
    }

    @Override
    public void writeTo(final OutputStream outstream) throws IOException {
        mapper.writeValue(outstream, sourceObject);
    }

    @Override
    public boolean isStreaming() {
        return false;
    }

    @Override
    public void consumeContent() throws IOException {
        throw new UnsupportedOperationException("Deprecated");
    }
}
