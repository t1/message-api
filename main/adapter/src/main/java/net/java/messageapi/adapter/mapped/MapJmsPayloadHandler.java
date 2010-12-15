package net.java.messageapi.adapter.mapped;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import javax.jms.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import net.java.messageapi.adapter.JmsPayloadHandler;
import net.java.messageapi.adapter.MessageCallFactory;

import com.google.common.collect.ImmutableMap;

/**
 * A {@link JmsPayloadHandler} that serializes the payload
 * as map message.
 */
@XmlRootElement
public class MapJmsPayloadHandler extends JmsPayloadHandler {

    @XmlTransient
    // FIXME not transient!
    private final Mapping mapping;

    public MapJmsPayloadHandler() {
        this(MappingBuilder.DEFAULT);
    }

    public MapJmsPayloadHandler(Mapping mapping) {
        this.mapping = mapping;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Message createJmsMessage(Object payload, Session session) throws JMSException {
        MapMessage message = session.createMapMessage();
        populateBody(message, (Map<String, Object>) payload);
        return message;
    }

    private void populateBody(MapMessage message, Map<String, Object> body) throws JMSException {
        for (Map.Entry<String, Object> e : body.entrySet()) {
            message.setObject(e.getKey(), e.getValue());
        }
    }

    @Override
    public Object toPayload(Class<?> api, Method method, Object[] args) {
        ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();

        String operationName = mapping.getOperationForMethod(method.getName());
        String operationField = mapping.getOperationMessageAttibute();
        builder.put(operationField, operationName);

        Object pojo = new MessageCallFactory<Object>(method).apply(args);
        builder.putAll(readFields(pojo));

        return builder.build();
    }

    private Map<String, Object> readFields(Object pojo) {
        ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();
        for (Field field : pojo.getClass().getDeclaredFields()) {
            String fieldName = field.getName();
            @SuppressWarnings("unchecked")
            FieldMapping<Object> fieldMapping = (FieldMapping<Object>) mapping.getMappingForField(fieldName);
            Object value = getField(pojo, field);
            if (value != null) {
                builder.put(fieldMapping.getAttributeName(), fieldMapping.marshal(value));
            }
        }
        return builder.build();
    }

    private Object getField(Object pojo, Field field) {
        try {
            field.setAccessible(true);
            return field.get(pojo);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
