package net.java.messageapi.adapter;

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
            public void visit(String name, Object value) throws JMSException {
                if (value instanceof String) {
                    message.setStringProperty(name, (String) value);
                } else if (value instanceof Boolean) {
                    message.setBooleanProperty(name, (Boolean) value);
                } else if (value instanceof Byte) {
                    message.setByteProperty(name, (Byte) value);
                } else if (value instanceof Character) {
                    message.setStringProperty(name, ((Character) value).toString());
                } else if (value instanceof Short) {
                    message.setShortProperty(name, (Short) value);
                } else if (value instanceof Integer) {
                    message.setIntProperty(name, (Integer) value);
                } else if (value instanceof Long) {
                    message.setLongProperty(name, (Long) value);
                } else if (value instanceof Float) {
                    message.setFloatProperty(name, (Float) value);
                } else if (value instanceof Double) {
                    message.setDoubleProperty(name, (Double) value);
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
