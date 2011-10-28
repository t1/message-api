package net.java.messageapi.adapter;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

import javax.jms.JMSException;

import net.java.messageapi.JmsProperty;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

class JmsPropertyScanner {

    /** The types that can directly be set on or read from a JMS message property */
    private static final Set<Class<?>> JMS_PROPERTY_TYPES = ImmutableSet.<Class<?>> of( //
            String.class, //
            Boolean.class, Boolean.TYPE, //
            Byte.class, Byte.TYPE, //
            Character.class, Character.TYPE,//
            Short.class, Short.TYPE, //
            Integer.class, Integer.TYPE, //
            Long.class, Long.TYPE, //
            Float.class, Float.TYPE, //
            Double.class, Double.TYPE //
    );

    interface Visitor {
        void visit(String name, Object value) throws JMSException;
    }

    private final List<Object> visited = Lists.newArrayList();
    private final Visitor visitor;

    public JmsPropertyScanner(Visitor visitor) {
        this.visitor = visitor;
    }

    public void scan(Object pojo) {
        try {
            scan(pojo, "", false);
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }

    private void scan(Object pojo, String prefix, boolean doAdd) throws JMSException {
        for (Field field : pojo.getClass().getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()))
                continue;
            Object value = getField(pojo, field);
            if (value == null)
                continue;
            boolean alreadSeen = !shouldVisit(value);
            if (alreadSeen)
                continue;
            boolean nestedDoAdd = doAdd || field.isAnnotationPresent(JmsProperty.class);
            String fieldName = prefix + field.getName();
            if (nestedDoAdd) {
                scan(fieldName, value);
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

    private void scan(String name, Object value) throws JMSException {
        if (JMS_PROPERTY_TYPES.contains(value.getClass())) {
            visitor.visit(name, value);
        } else if (value instanceof Collection) {
            scan((Collection<?>) value, name);
        } else if (value.getClass().isArray()) {
            scan((Object[]) value, name);
        } else {
            scan(value, name + "/", true);
        }
    }

    private void scan(Collection<?> collection, String prefix) throws JMSException {
        int i = 0;
        for (Iterator<?> iterator = collection.iterator(); iterator.hasNext(); i++) {
            Object element = iterator.next();
            scan(prefix + "[" + i + "]", element);
        }
    }

    private void scan(Object[] list, String prefix) throws JMSException {
        for (int i = 0; i < list.length; i++) {
            Object element = list[i];
            scan(prefix + "[" + i + "]", element);
        }
    }

    private boolean shouldVisit(Object value) {
        // IdentitySet would be nicer
        for (Object element : visited) {
            if (value == element) {
                return false;
            }
        }
        visited.add(value);
        return true;
    }
}