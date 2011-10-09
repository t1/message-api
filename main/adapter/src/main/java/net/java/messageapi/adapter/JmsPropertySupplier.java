package net.java.messageapi.adapter;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Set;

import javax.jms.JMSException;
import javax.jms.Message;

import net.java.messageapi.JmsProperty;

import com.google.common.collect.ImmutableSet;

/**
 * Provides the header fields that are annotated as {@link JmsProperty}
 */
class JmsPropertySupplier implements JmsHeaderSupplier {
    @Override
    public void addTo(Message message, Object pojo) throws JMSException {
        for (Field field : pojo.getClass().getDeclaredFields()) {
            Object value = getField(pojo, field);
            if (value == null)
                continue;
            if (field.isAnnotationPresent(JmsProperty.class)) {
                // TODO add other types
                if (value instanceof String) {
                    message.setStringProperty(field.getName(), (String) value);
                } else if (value instanceof Integer) {
                    message.setIntProperty(field.getName(), (Integer) value);
                } else if (value instanceof Boolean) {
                    message.setBooleanProperty(field.getName(), (Boolean) value);
                } else if (value instanceof Long) {
                    message.setLongProperty(field.getName(), (Long) value);
                }
            }
            if (!Modifier.isStatic(field.getModifiers()) && !isPrimitive(field.getType())) {
                addTo(message, value);
            }
        }
    }

    private Object getField(Object pojo, Field field) {
        try {
            field.setAccessible(true);
            return field.get(pojo);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static final Set<Class<?>> PRIMITIVE_TYPES = ImmutableSet.<Class<?>> of(Boolean.class,
            Byte.class, Character.class, Short.class, Long.class, Float.class, Double.class,
            String.class);

    private boolean isPrimitive(Class<?> type) {
        return type.isPrimitive() || PRIMITIVE_TYPES.contains(type);
    }

    @Override
    public boolean equals(Object other) {
        return other != null && other.getClass().equals(this.getClass());
    }
}
