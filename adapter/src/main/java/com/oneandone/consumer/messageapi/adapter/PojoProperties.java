package com.oneandone.consumer.messageapi.adapter;

import java.lang.reflect.Method;
import java.util.Map;

import com.google.common.collect.Maps;

class PojoProperties {

    private final Map<String, Object> map;

    public static PojoProperties of(Object pojo) {
        Map<String, Object> result = Maps.newHashMap();
        for (Method method : pojo.getClass().getMethods()) {
            if (isGetter(method)) {
                try {
                    String propertyName = toPropertyName(method);
                    Object value = method.invoke(pojo);
                    result.put(propertyName, value);
                } catch (Exception e) {
                    throw new RuntimeException("can't invoke " + method, e);
                }
            }
        }
        return new PojoProperties(result);
    }

    private static String toPropertyName(Method method) {
        String name = method.getName().substring(3); // "get"
        return Character.toLowerCase(name.charAt(0)) + name.substring(1);
    }

    private static boolean isGetter(Method method) {
        return true //
                && method.getParameterTypes().length == 0
                && method.getName().startsWith("get")
                && method.getName().length() > 3
                && method.getReturnType() != Void.TYPE
                && Character.isUpperCase(method.getName().charAt(3))
                && method.getDeclaringClass() != Object.class // getClass
        ;
    }

    private PojoProperties(Map<String, Object> map) {
        this.map = map;
    }

    public Object getValue(String name) {
        return map.get(name);
    }

    public boolean hasProperty(String name) {
        return map.containsKey(name);
    }

    public int size() {
        return map.size();
    }

    @Override
    public String toString() {
        return "PojoProperties" + map;
    }
}
