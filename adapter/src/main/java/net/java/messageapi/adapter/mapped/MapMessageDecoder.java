package net.java.messageapi.adapter.mapped;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;

import javax.jms.*;

import net.java.messageapi.MessageApi;
import net.java.messageapi.adapter.PojoInvoker;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * Takes a {@link MapMessage}, deserializes it and calls the corresponding method in an
 * implementation of some {@link MessageApi}. If a parameter is missing in the map, zero or @code
 * null is used (depending on the type).
 * 
 * TODO optionally fail when message fields are missing and/or unexpected
 * 
 * @param <T>
 *            the {@link MessageApi} interface that the calls are for and the <code>impl</code>
 *            implements.
 */
public class MapMessageDecoder<T> implements MessageListener {

    public static <T> MapMessageDecoder<T> create(Class<T> api, T impl, String operationField) {
        return create(api, impl, new DefaultMapping(operationField));
    }

    public static <T> MapMessageDecoder<T> create(Class<T> api, T impl, Mapping mapping) {
        return new MapMessageDecoder<T>(api, impl, mapping);
    }

    private final Class<T> api;
    private final PojoInvoker<T> invoker;
    private final Mapping mapping;

    public MapMessageDecoder(Class<T> api, T impl, Mapping mapping) {
        if (mapping == null)
            throw new RuntimeException("mapping must not be null");
        this.api = api;
        this.invoker = new PojoInvoker<T>(api, impl);
        this.mapping = mapping;
    }

    @Override
    public void onMessage(Message message) {
        receive(convert((MapMessage) message));
    }

    private Map<String, String> convert(MapMessage message) {
        ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
        try {
            @SuppressWarnings("unchecked")
            Enumeration<String> mapNames = message.getMapNames();
            while (mapNames.hasMoreElements()) {
                String name = mapNames.nextElement();
                String value = message.getString(name);
                if (value == null)
                    throw new RuntimeException("no value for field [" + name + "]");
                builder.put(name, value);
            }
            return builder.build();
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }

    private void receive(Map<String, String> source) {
        String methodName = getMethodName(source);
        try {
            Object pojo = constructPojo(methodName);
            populatePojo(pojo, source);
            invoker.invoke(pojo);
        } catch (Exception e) {
            throw new RuntimeException("Could not receive " + source + " for " + api.getName()
                    + " at " + methodName, e);
        }
    }

    private String getMethodName(Map<String, String> body) {
        String operationMessageAttibute = mapping.getOperationMessageAttibute();
        if (!body.containsKey(operationMessageAttibute)) {
            throw new IllegalArgumentException("Body does not contain field "
                    + operationMessageAttibute + " for operation name!");
        }
        String operationName = body.get(operationMessageAttibute).toString();
        return mapping.getMethodForOperation(operationName);
    }

    private Object constructPojo(String methodName) throws Exception {
        String className = getClassName(methodName);
        Class<?> pojoClass = Class.forName(className);
        Constructor<?> ctor = pojoClass.getDeclaredConstructor();
        ctor.setAccessible(true);
        return ctor.newInstance();
    }

    private String getClassName(String methodName) {
        return api.getPackage().getName() + "." //
                + Character.toUpperCase(methodName.charAt(0)) //
                + methodName.substring(1);
    }

    private void populatePojo(Object pojo, Map<String, String> body) throws Exception {
        for (Field argumentField : getFields(pojo)) {
            String fieldName = argumentField.getName();
            FieldMapping<?> fieldMapping = mapping.getMappingForField(fieldName);
            if (body.containsKey(fieldMapping.getAttributeName())) {
                String originalValue = body.get(fieldMapping.getAttributeName());
                Object convertedValue = fieldMapping.unmarshal(originalValue);
                if (convertedValue instanceof String) {
                    convertedValue = convertType((String) convertedValue, argumentField.getType());
                }
                setField(pojo, argumentField, convertedValue);
            } else if (fieldMapping.hasDefaultValue()) {
                setField(pojo, argumentField, fieldMapping.getDefaultValue());
            } else {
                // ignore... see class comment
            }
        }
    }

    private void setField(Object target, Field field, Object value) throws IllegalAccessException {
        field.setAccessible(true);
        field.set(target, value);
    }

    private Collection<Field> getFields(Object pojo) {
        ImmutableSet.Builder<Field> builder = ImmutableSet.builder();
        for (Field f : pojo.getClass().getDeclaredFields()) {
            builder.add(f);
        }
        return builder.build();
    }

    private Object convertType(String convertable, Class<?> type) {
        if (type == Byte.class || type == Byte.TYPE) {
            return Byte.valueOf(convertable);
        }
        if (type == Short.class || type == Short.TYPE) {
            return Short.valueOf(convertable);
        }
        if (type == Integer.class || type == Integer.TYPE) {
            return Integer.valueOf(convertable);
        }
        if (type == Long.class || type == Long.TYPE) {
            return Long.valueOf(convertable);
        }
        if (type == Float.class || type == Float.TYPE) {
            return Float.valueOf(convertable);
        }
        if (type == Double.class || type == Double.TYPE) {
            return Double.valueOf(convertable);
        }
        if (type == Character.class || type == Character.TYPE) {
            return Character.valueOf(convertable.charAt(0));
        }
        if (type == Boolean.class || type == Boolean.TYPE) {
            return Boolean.valueOf(convertable);
        }
        if (type == String.class) {
            return convertable;
        }
        // TODO Think about a converter registry or something like that
        throw new RuntimeException("can only convert to primitive types, but type is " + type);
    }
}
