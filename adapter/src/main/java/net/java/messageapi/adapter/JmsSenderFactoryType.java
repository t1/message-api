/**
 * 
 */
package net.java.messageapi.adapter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import net.java.messageapi.adapter.mapped.JmsMapSenderFactory;
import net.java.messageapi.adapter.xml.JmsXmlSenderFactory;

public enum JmsSenderFactoryType {
    XML(JmsXmlSenderFactory.class),

    MAP(JmsMapSenderFactory.class);

    public static final JmsSenderFactoryType DEFAULT = XML;

    private final Constructor<?> constructor;

    private JmsSenderFactoryType(Class<?> factory) {
        this.constructor = getConstructor(factory);
    }

    private Constructor<?> getConstructor(Class<?> factory) {
        try {
            return factory.getConstructor(Class.class, JmsConfig.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> AbstractJmsSenderFactory<T, ?> createFactory(Class<T> api, JmsConfig config) {
        try {
            return (AbstractJmsSenderFactory<T, ?>) constructor.newInstance(api, config);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
