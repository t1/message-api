package com.oneandone.consumer.messageapi.adapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import com.google.common.collect.Lists;
import com.oneandone.consumer.messageapi.MessageApi;
import com.oneandone.consumer.messageapi.reflection.Parameter;

/**
 * Calls methods from an implementation of an {@link MessageApi} by converting instances of the
 * generated POJOs back to method calls.
 */
public class PojoInvoker<T> {

    private final Class<T> api;
    private final T impl;

    public PojoInvoker(Class<T> api, T impl) {
        if (api == null)
            throw new RuntimeException("api must not be null");
        if (impl == null)
            throw new RuntimeException("impl must not be null");
        if (!api.isInstance(impl))
            throw new IllegalArgumentException(api.getName() + " is not implemented by "
                    + impl.getClass());
        this.api = api;
        this.impl = impl;
    }

    public void invoke(Object pojo) {
        PojoProperties pojoProperties = PojoProperties.of(pojo);

        String methodName = getMethodNameFor(pojo);

        for (Method method : api.getMethods()) {
            if (method.getName().equals(methodName)) {
                List<Parameter> methodParameters = Parameter.allOf(method);
                if (matches(methodParameters, pojoProperties)) {
                    invoke(method, methodParameters, pojoProperties);
                    return;
                }
            }
        }
        throw new RuntimeException("method " + methodName + " with properties " + pojoProperties
                + " not found in " + api);
    }

    private String getMethodNameFor(Object pojo) {
        String name = pojo.getClass().getSimpleName();
        return Character.toLowerCase(name.charAt(0)) + name.substring(1);
    }

    private boolean matches(List<Parameter> methodParameters, PojoProperties pojoProperties) {
        if (pojoProperties.size() != methodParameters.size())
            return false;
        for (Parameter parameter : methodParameters) {
            String name = parameter.getName();
            if (!pojoProperties.hasProperty(name))
                return false;
            Object propertyValue = pojoProperties.getValue(name);
            if (!parameter.isCallable(propertyValue)) { // is not isValidValue more readable?
                return false;
            }
        }
        return true;
    }

    private void invoke(Method method, List<Parameter> methodParameters,
            PojoProperties pojoProperties) {
        try {
            method.invoke(impl, getArgs(methodParameters, pojoProperties));
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

    private Object[] getArgs(List<Parameter> methodParameters, PojoProperties pojoProperties) {
        List<Object> result = Lists.newArrayList();
        for (Parameter parameter : methodParameters) {
            String name = parameter.getName();
            Object value = pojoProperties.getValue(name);
            result.add(value);
        }
        return result.toArray();
    }
}
