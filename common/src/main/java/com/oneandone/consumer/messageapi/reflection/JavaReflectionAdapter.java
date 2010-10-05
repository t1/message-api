/**
 * 
 */
package com.oneandone.consumer.messageapi.reflection;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

class JavaReflectionAdapter extends ReflectionAdapter<Method> {

    public JavaReflectionAdapter(Method method) {
        super(method);
    }

    @Override
    protected String getMethodName(Method method) {
        return method.getName();
    }

    @Override
    protected List<Method> siblings(Method method) {
        return Arrays.asList(method.getDeclaringClass().getMethods());
    }

    @Override
    public String getPackage() {
        return method.getDeclaringClass().getPackage().getName();
    }

    @Override
    protected List<?> getParameters() {
        return Arrays.asList(method.getParameterTypes());
    }

    @Override
    protected String typeNameOf(Object object) {
        return ((Class<?>) object).getName();
    }
}