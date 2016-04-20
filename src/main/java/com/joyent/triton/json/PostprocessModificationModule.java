package com.joyent.triton.json;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.ser.std.BeanSerializerBase;
import com.joyent.triton.domain.Instance;
import com.joyent.triton.queryfilters.InstanceFilter;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Jackson module for dynamically injecting fields based on the contents of
 * the source object.
 *
 * @author <a href="https://github.com/dekobon">Elijah Zupancic</a>
 * @since 1.0.0
 */
public class PostprocessModificationModule extends SimpleModule {
    /**
     * Immutable {@link Set} of classes to modify.
     */
    private static final Set<Class<?>> MODIFIABLE_CLASSES;

    static {
        final Set<Class<?>> set = new HashSet<Class<?>>(2);
        set.add(Instance.class);
        set.add(InstanceFilter.class);
        MODIFIABLE_CLASSES = Collections.unmodifiableSet(set);
    }

    @Override
    public void setupModule(final SetupContext context) {
        super.setupModule(context);

        context.addBeanSerializerModifier(new BeanSerializerModifier() {

            public JsonSerializer<?> modifySerializer(
                    final SerializationConfig config,
                    final BeanDescription beanDesc,
                    final JsonSerializer<?> serializer) {
                Class<?> valueClass = beanDesc.getType().getRawClass();

                if (serializer instanceof BeanSerializerBase
                        && MODIFIABLE_CLASSES.contains(valueClass)) {
                    return new FlatteningModifySerializer(
                            (BeanSerializerBase) serializer);
                }

                return serializer;
            }
        });
    }
}
