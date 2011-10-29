package net.java.messageapi.adapter;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

import javax.jms.JMSException;
import javax.jms.Message;

import net.java.messageapi.JmsProperty;

/**
 * Provides the header fields that are annotated as {@link JmsProperty}
 */
class JmsPropertySupplier implements JmsHeaderSupplier {

    @Override
    public void addTo(final Message message, Object pojo) throws JMSException {
        JmsPropertyScanner scanner = new JmsPropertyScanner(new JmsPropertyScanner.Visitor() {
            @Override
            public void visit(String propertyName, Object container, Field field, Object index)
                    throws JMSException, IllegalAccessException {
                Object value = field.get(container);
                if (value == null) {
                    // do not add
                } else if (value instanceof String) {
                    message.setStringProperty(propertyName, (String) value);
                } else if (value instanceof Boolean) {
                    message.setBooleanProperty(propertyName, (Boolean) value);
                } else if (value instanceof Byte) {
                    message.setByteProperty(propertyName, (Byte) value);
                } else if (value instanceof Character) {
                    message.setStringProperty(propertyName, ((Character) value).toString());
                } else if (value instanceof Short) {
                    message.setShortProperty(propertyName, (Short) value);
                } else if (value instanceof Integer) {
                    message.setIntProperty(propertyName, (Integer) value);
                } else if (value instanceof Long) {
                    message.setLongProperty(propertyName, (Long) value);
                } else if (value instanceof Float) {
                    message.setFloatProperty(propertyName, (Float) value);
                } else if (value instanceof Double) {
                    message.setDoubleProperty(propertyName, (Double) value);
                } else if (value instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<String> list = (List<String>) value;
                    int i = (Integer) index;
                    message.setStringProperty(propertyName, list.get(i));
                } else if (value.getClass().isArray()) {
                    String[] array = (String[]) value;
                    int i = (Integer) index;
                    message.setStringProperty(propertyName, array[i]);
                } else if (value instanceof Set) {
                    @SuppressWarnings("unchecked")
                    Set<String> set = (Set<String>) value;
                    int i = 0;
                    for (String element : set) {
                        if (i++ == (Integer) index) {
                            message.setStringProperty(propertyName, element);
                            break;
                        }
                    }
                } else {
                    throw new RuntimeException("don't know how to set property " + propertyName
                            + " to the " + value.getClass().getName() + " [" + value + "]");
                }
            }
        });
        scanner.scan(pojo);
    }

    @Override
    public boolean equals(Object other) {
        return other != null && other.getClass().equals(this.getClass());
    }

    @Override
    public int hashCode() {
        return 1; // that's okay... all JmsPropertySuppliers are equal!
    }
}
