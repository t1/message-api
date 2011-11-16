package net.java.messageapi.adapter;

import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jms.*;

import net.java.messageapi.MessageApi;

import com.google.common.collect.*;

/**
 * Takes a {@link ObjectMessage}, deserializes it and calls the corresponding method in an
 * implementation of the matching {@link MessageApi}.
 * 
 * @param <T>
 *            the {@link MessageApi} interface that the calls are for and the <code>impl</code>
 *            implements.
 */
public class ObjectMessageDecoder<T> implements MessageListener {

    public static <T> ObjectMessageDecoder<T> of(Class<T> api, T impl) {
        return new ObjectMessageDecoder<T>(api, impl);
    }

    private final PojoInvoker<T> invoker;

    private ObjectMessageDecoder(Class<T> api, T impl) {
        this.invoker = new PojoInvoker<T>(api, impl);
    }

    @Override
    public void onMessage(final Message message) {
        Object pojo = getPayload(message);
        addHeaderOnlyProperties(message, pojo);
        invoker.invoke(pojo);
    }

    public Object getPayload(final Message message) {
        try {
            return ((ObjectMessage) message).getObject();
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }

    private void addHeaderOnlyProperties(final Message message, Object pojo) {
        new JmsPropertyScanner(new JmsPropertyScanner.Visitor() {
            @Override
            public void visit(String propertyName, Object container, Field field)
                    throws JMSException, IllegalAccessException {
                // TODO something similar has to be done for mapped messages; refactor
                Class<?> type = field.getType();
                if (String.class.equals(type)) {
                    String value = message.getStringProperty(propertyName);
                    field.set(container, value);
                } else if (Boolean.class.equals(type) || Boolean.TYPE.equals(type)) {
                    boolean value = message.getBooleanProperty(propertyName);
                    field.set(container, value);
                } else if (Byte.class.equals(type) || Byte.TYPE.equals(type)) {
                    byte value = message.getByteProperty(propertyName);
                    field.set(container, value);
                } else if (Short.class.equals(type) || Short.TYPE.equals(type)) {
                    short value = message.getShortProperty(propertyName);
                    field.set(container, value);
                } else if (Integer.class.equals(type) || Integer.TYPE.equals(type)) {
                    int value = message.getIntProperty(propertyName);
                    field.set(container, value);
                } else if (Long.class.equals(type) || Long.TYPE.equals(type)) {
                    long value = message.getLongProperty(propertyName);
                    field.set(container, value);
                } else if (Float.class.equals(type) || Float.TYPE.equals(type)) {
                    float value = message.getFloatProperty(propertyName);
                    field.set(container, value);
                } else if (Double.class.equals(type) || Double.TYPE.equals(type)) {
                    double value = message.getDoubleProperty(propertyName);
                    field.set(container, value);
                } else if (List.class.equals(type)) {
                    List<String> list = getListProperty(propertyName);
                    field.set(container, list);
                } else if (type.isArray()) {
                    List<String> list = getListProperty(propertyName);
                    field.set(container, list.toArray(new String[0]));
                } else if (Set.class.equals(type)) {
                    Set<String> set = getSetProperty(propertyName);
                    field.set(container, set);
                } else if (Map.class.equals(type)) {
                    Map<String, String> map = getMapProperty(propertyName);
                    field.set(container, map);
                } else {
                    throw new RuntimeException("don't know how to set " + field
                            + " to the header-only jms propery " + propertyName);
                }
            }

            private List<String> getListProperty(String propertyName) throws JMSException {
                List<String> list = Lists.newArrayList();
                Pattern pattern = Pattern.compile("^" + propertyName + "\\[([0-9]+)\\]$");
                Enumeration<String> propertyNames = message.getPropertyNames();
                while (propertyNames.hasMoreElements()) {
                    String name = propertyNames.nextElement();
                    Matcher matcher = pattern.matcher(name);
                    if (matcher.matches()) {
                        int index = Integer.parseInt(matcher.group(1));
                        String value = message.getStringProperty(name);
                        while (list.size() <= index)
                            list.add(null);
                        list.set(index, value);
                    }
                }
                return list;
            }

            private Set<String> getSetProperty(String propertyName) throws JMSException {
                Set<String> set = Sets.newHashSet();
                Pattern pattern = Pattern.compile("^" + propertyName + "\\[([0-9]+)\\]$");
                Enumeration<String> propertyNames = message.getPropertyNames();
                while (propertyNames.hasMoreElements()) {
                    String name = propertyNames.nextElement();
                    Matcher matcher = pattern.matcher(name);
                    if (matcher.matches()) {
                        String value = message.getStringProperty(name);
                        set.add(value);
                    }
                }
                return set;
            }

            private Map<String, String> getMapProperty(String propertyName) throws JMSException {
                ImmutableMap.Builder<String, String> map = ImmutableMap.builder();
                Pattern pattern = Pattern.compile("^" + propertyName + "\\[([^\\]]+)\\]$");
                Enumeration<String> propertyNames = message.getPropertyNames();
                while (propertyNames.hasMoreElements()) {
                    String name = propertyNames.nextElement();
                    Matcher matcher = pattern.matcher(name);
                    if (matcher.matches()) {
                        String key = matcher.group(1);
                        String value = message.getStringProperty(name);
                        map.put(key, value);
                    }
                }
                return map.build();
            }
        }).scan(pojo);
    }
}
