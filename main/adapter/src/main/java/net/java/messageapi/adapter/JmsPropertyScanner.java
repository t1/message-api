package net.java.messageapi.adapter;

import java.lang.reflect.*;
import java.util.*;

import javax.jms.JMSException;

import net.java.messageapi.JmsProperty;

class JmsPropertyScanner {

    /** The types that can directly be set on or read from a JMS message property */
    private static final List<Class<?>> JMS_PROPERTY_TYPES = Arrays.asList(new Class<?>[] { //
    String.class, //
            Boolean.class, Boolean.TYPE, //
            Byte.class, Byte.TYPE, //
            Character.class, Character.TYPE,//
            Short.class, Short.TYPE, //
            Integer.class, Integer.TYPE, //
            Long.class, Long.TYPE, //
            Float.class, Float.TYPE, //
            Double.class, Double.TYPE //
    });

    interface Visitor {
        public void visit(String propertyName, Object container, Field field) throws JMSException,
                IllegalAccessException;
    }

    private final Visitor visitor;

    public JmsPropertyScanner(Visitor visitor) {
        this.visitor = visitor;
    }

    public void scan(Object object) {
        try {
            scan(object, "", false, new ArrayList<Object>());
        } catch (JMSException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void scan(Object object, String string, boolean nestedAdd, List<Object> visited) throws JMSException,
            IllegalAccessException {
        for (Field field : object.getClass().getDeclaredFields()) {
            String subPrefix = string + field.getName();
            boolean isAnnotated = field.isAnnotationPresent(JmsProperty.class);
            if (Modifier.isStatic(field.getModifiers())) {
                assert !isAnnotated : "field " + field + " is static but annotated as JmsProperty";
                continue;
            }
            field.setAccessible(true);

            if (JMS_PROPERTY_TYPES.contains(field.getType())) {
                if (nestedAdd || isAnnotated) {
                    visitor.visit(subPrefix, object, field);
                }
            } else if (Collection.class.isAssignableFrom(field.getType())) {
                if (nestedAdd || isAnnotated) {
                    visitor.visit(subPrefix, object, field);
                }
            } else if (field.getType().isArray()) {
                if (nestedAdd || isAnnotated) {
                    visitor.visit(subPrefix, object, field);
                }
            } else if (Map.class.isAssignableFrom(field.getType())) {
                if (nestedAdd || isAnnotated) {
                    visitor.visit(subPrefix, object, field);
                }
            } else {
                Object value = field.get(object);
                if (value != null && !contains(visited, value)) {
                    visited.add(value);
                    scan(value, subPrefix + "/", nestedAdd || isAnnotated, visited);
                }
            }
        }
    }

    private boolean contains(List<Object> visited, Object value) {
        // a simple IdentitySet would be nicer
        for (Object object : visited) {
            if (object == value) {
                return true;
            }
        }
        return false;
    }
}
