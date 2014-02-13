package net.java.messageapi.reflection;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

class JavaReflectionAdapter extends ReflectionAdapter<Method> {

    public JavaReflectionAdapter(Method method) {
        super(method);
    }

    @Override
    protected String getSimpleMethodNameOf(Method otherMethod) {
        return otherMethod.getName();
    }

    @Override
    protected List<Method> siblings() {
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

    @Override
    public String getDeclaringType() {
        return method.getDeclaringClass().getName();
    }
}