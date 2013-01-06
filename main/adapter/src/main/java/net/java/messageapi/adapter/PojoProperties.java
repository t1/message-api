package net.java.messageapi.adapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlType;

import net.java.messageapi.reflection.Parameter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

class PojoProperties {
    final Logger log = LoggerFactory.getLogger(PojoProperties.class);

    private final List<Object> properties;

    public static PojoProperties of(Object pojo) {
        Class<? extends Object> pojoType = pojo.getClass();
        List<String> propOrder = getPropOrder(pojoType);
        List<Object> result = Lists.newArrayList();
        for (Method method : pojoType.getMethods()) {
            if (!isGetter(method))
                continue;

            try {
                Object value = method.invoke(pojo);
                if (propOrder == null) {
                    result.add(value);
                } else {
                    String propertyName = toPropertyName(method);
                    int index = propOrder.indexOf(propertyName);
                    if (index < 0)
                        throw new RuntimeException("property [" + propertyName + "] not found in propOrder "
                                + propOrder);
                    ensureSize(result, index);
                    result.set(index, value);
                }
            } catch (Exception e) {
                throw new RuntimeException("can't invoke " + method, e);
            }
        }
        return new PojoProperties(result);
    }

    private static List<String> getPropOrder(Class<?> pojoType) {
        PropOrder propOrderAnnotation = pojoType.getAnnotation(PropOrder.class);
        if (propOrderAnnotation != null)
            return Arrays.asList(propOrderAnnotation.value());
        XmlType xmlType = pojoType.getAnnotation(XmlType.class);
        if (xmlType == null || xmlType.propOrder() == null)
            return null;
        String[] propOrder = xmlType.propOrder();
        if (propOrder.length == 0)
            return null;
        return Arrays.asList(propOrder);
    }

    private static boolean isGetter(Method method) {
        return true //
                && method.getParameterTypes().length == 0
                && method.getName().startsWith("get")
                && method.getName().length() > 3
                && method.getReturnType() != Void.TYPE
                && Character.isUpperCase(method.getName().charAt(3)) && method.getDeclaringClass() != Object.class // getClass
        ;
    }

    private static String toPropertyName(Method method) {
        String name = method.getName().substring(3); // "get"
        return Character.toLowerCase(name.charAt(0)) + name.substring(1);
    }

    private static void ensureSize(List<Object> result, int size) {
        while (result.size() <= size) {
            result.add(null);
        }
    }

    private PojoProperties(List<Object> properties) {
        this.properties = properties;
    }

    @Override
    public String toString() {
        return "PojoProperties" + properties;
    }

    boolean matches(List<Parameter> methodParameters) {
        if (properties.size() != methodParameters.size()) {
            log.debug("properties sizes mismatch: {} vs. {}", methodParameters, this);
            return false;
        }
        for (int i = 0; i < properties.size(); i++) {
            Parameter parameter = methodParameters.get(i);
            Object property = properties.get(i);
            if (!parameter.isAssignable(property)) {
                log.debug("not assignable [{}]", property.getClass());
                return false;
            }
        }
        return true;
    }

    public void invoke(Object impl, Method method) {
        try {
            method.invoke(impl, getArgs());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof RuntimeException)
                throw (RuntimeException) e.getCause();
            if (e.getCause() instanceof Error)
                throw (Error) e.getCause();
            throw new RuntimeException(e.getCause());
        }
    }

    public Object[] getArgs() {
        List<Object> result = Lists.newArrayList();
        for (int i = 0; i < properties.size(); i++) {
            Object property = properties.get(i);
            result.add(property);
        }
        return result.toArray();
    }
}
