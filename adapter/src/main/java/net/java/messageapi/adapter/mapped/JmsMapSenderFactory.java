package net.java.messageapi.adapter.mapped;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import javax.jms.*;

import net.java.messageapi.MessageApi;
import net.java.messageapi.adapter.*;

import com.google.common.collect.ImmutableMap;

/**
 * A {@link MessageSenderFactory} that sends the message to a local JMS queue, with the call
 * serialized as the body of a map message.
 * 
 * @see MessageApi
 */
public class JmsMapSenderFactory<T> extends AbstractJmsSenderFactory<T, Map<String, Object>> {

    public static <T> JmsMapSenderFactory<T> create(Class<T> api, JmsConfig config, Mapping mapping) {
        return new JmsMapSenderFactory<T>(api, config, mapping);
    }

    private final Mapping mapping;

    public JmsMapSenderFactory(Class<T> api, JmsConfig config) {
        this(api, config, MappingBuilder.DEFAULT);
    }

    public JmsMapSenderFactory(Class<T> api, JmsConfig config, Mapping mapping) {
        super(api, config);
        this.mapping = mapping;
    }

    @Override
    protected Message createJmsMessage(Map<String, Object> payload, Session session)
            throws JMSException {
        MapMessage message = session.createMapMessage();
        populateBody(message, payload);
        return message;
    }

    @Override
    protected Map<String, Object> toPayload(Method method, Object[] args) {
        ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();

        String operationName = mapping.getOperationForMethod(method.getName());
        String operationField = mapping.getOperationMessageAttibute();
        builder.put(operationField, operationName);

        Object pojo = new MessageCallFactory<Object>(method).apply(args);
        builder.putAll(readFields(pojo));

        return builder.build();
    }

    private void populateBody(MapMessage message, Map<String, Object> body) throws JMSException {
        for (Map.Entry<String, Object> e : body.entrySet()) {
            message.setObject(e.getKey(), e.getValue());
        }
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
