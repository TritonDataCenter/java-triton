package com.joyent.triton.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.impl.BeanAsArraySerializer;
import com.fasterxml.jackson.databind.ser.impl.ObjectIdWriter;
import com.fasterxml.jackson.databind.ser.std.BeanSerializerBase;
import com.joyent.triton.domain.MetadataEnabled;
import com.joyent.triton.domain.Taggable;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * Serializer class that flattens the tags and metadata map so that it fits
 * the format of tag.$name and metadata.$name that the CloudAPI expects.
 *
 * @author <a href="https://github.com/dekobon">Elijah Zupancic</a>
 * @since 1.0.0
 */
class FlatteningModifySerializer extends BeanSerializerBase {

    private static final long serialVersionUID = -4611878621406309864L;

    /**
     * Creates a new instance of {@link FlatteningModifySerializer}.
     *
     * @param source base serializer to back this implementation
     */
    FlatteningModifySerializer(final BeanSerializerBase source) {
        super(source);
    }

    /**
     * Creates a new instance of {@link FlatteningModifySerializer} chained
     * to another {@link FlatteningModifySerializer}.
     *
     * @param source object to chain to
     * @param objectIdWriter Object that knows how to serialize object ids
     */
    FlatteningModifySerializer(final FlatteningModifySerializer source,
                               final ObjectIdWriter objectIdWriter) {
        super(source, objectIdWriter);
    }

    /**
     * Creates a new instance of {@link FlatteningModifySerializer} chained
     * to another {@link FlatteningModifySerializer}.
     *
     * @param source object to chain to
     * @param toIgnore ignored fields
     */
    FlatteningModifySerializer(final FlatteningModifySerializer source,
                               final String[] toIgnore) {
        super(source, toIgnore);
    }

    /**
     * Creates a new instance of {@link FlatteningModifySerializer}.
     *
     * @param source base serializer to back this implementation
     * @param objectIdWriter Object that knows how to serialize object ids
     * @param filterId property filter id
     */
    protected FlatteningModifySerializer(final BeanSerializerBase source,
                                         final ObjectIdWriter objectIdWriter,
                                         final Object filterId) {
        super(source, objectIdWriter, filterId);
    }

    @Override
    public BeanSerializerBase withObjectIdWriter(final ObjectIdWriter objectIdWriter) {
        return new FlatteningModifySerializer(this, objectIdWriter);
    }

    /**
     * Copied from the underlying Jackson implementation of BeanSerializer.
     *
     * Implementation has to check whether as-array serialization
     * is possible reliably; if (and only if) so, will construct
     * a {@link BeanAsArraySerializer}, otherwise will return this
     * serializer as is.
     */
    @Override
    protected BeanSerializerBase asArraySerializer() {
        /* Can not:
         *
         * - have Object Id (may be allowed in future)
         * - have "any getter"
         * - have per-property filters
         */
        if ((_objectIdWriter == null)
                && (_anyGetterWriter == null)
                && (_propertyFilterId == null)
                ) {
            return new BeanAsArraySerializer(this);
        }
        // already is one, so:
        return this;
    }

    @Override
    public BeanSerializerBase withFilterId(final Object filterId) {
        return new FlatteningModifySerializer(this, _objectIdWriter, filterId);
    }

    @Override
    protected BeanSerializerBase withIgnorals(final String[] toIgnore) {
        return new FlatteningModifySerializer(this, toIgnore);
    }

    @Override
    public void serialize(final Object bean,
                          final JsonGenerator jgen,
                          final SerializerProvider provider) throws IOException {
        jgen.writeStartObject();
        serializeFields(bean, jgen, provider);

        if (bean instanceof Taggable) {
            @SuppressWarnings({"unchecked", "rawtypes"})
            Taggable instance = (Taggable)bean;

            @SuppressWarnings("unchecked")
            final Map<String, String> tags = (Map<String, String>)instance.getTags();
            flattenMap("tag", jgen, tags);
        }

        if (bean instanceof MetadataEnabled) {
            @SuppressWarnings({"unchecked", "rawtypes"})
            MetadataEnabled instance = (MetadataEnabled) bean;

            @SuppressWarnings("unchecked")
            final Map<String, String> metadata = (Map<String, String>)instance.getMetadata();
            flattenMap("metadata", jgen, metadata);
        }

        jgen.writeEndObject();
    }

    /**
     * Flattens a map's values such that they are represented in the target JSON
     * as field_name.key_name.
     *
     * @param prefix the field name that we are collapsing all entries to
     * @param jgen Object used to programmatically generate JSON
     * @param map map to flatten to single fields
     * @throws IOException thrown when we are unable to serialize map
     */
    private void flattenMap(final String prefix,
                            final JsonGenerator jgen,
                            final Map<String, String> map)
            throws IOException {
        if (map == null) {
            return;
        }

        final Set<Map.Entry<String, String>> entrySet = map.entrySet();

        for (Map.Entry<String, String> entry : entrySet) {
            final String field = String.format("%s.%s", prefix, entry.getKey());
            final String value = entry.getValue();
            jgen.writeStringField(field, value);
        }
    }
}
