package net.java.messageapi.adapter;

import java.lang.reflect.*;
import java.util.Arrays;

import net.java.messageapi.reflection.ReflectionAdapter;

import com.google.common.base.Function;

/**
 * Creates instances of the class corresponding to a method invocation.
 */
public class MessageCallFactory<T> implements Function<Object[], T> {

    private final Method method;
    private final Class<T> pojoClass;

    public MessageCallFactory(Method method) {
        this.method = method;
        this.pojoClass = getType(method);
    }

    private Class<T> getType(Method method) {
        Class<?> result;
        try {
            String pojoClassName = ReflectionAdapter.of(method).getMethodNameAsFullyQualifiedClassName();
            result = Class.forName(pojoClassName);
        } catch (ClassNotFoundException e) {
            result = new MethodAsClassGenerator(method).get();
        }
        @SuppressWarnings("unchecked")
        Class<T> classT = (Class<T>) result;
        return classT;
    }

    public T apply(Object[] args) {
        try {
            Class<?>[] argTypes = getArgTypes(args);
            Constructor<?> pojoConstructor = findConstructor(pojoClass, argTypes);
            return pojoClass.cast(pojoConstructor.newInstance(args));
        } catch (Exception e) {
            throw new RuntimeException("can't produce a pojo matching " + method, e);
        }
    }

    private Class<?>[] getArgTypes(Object[] args) {
        if (args == null)
            return null;
        Class<?>[] argTypes = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            argTypes[i] = (args[i] == null) ? null : args[i].getClass();
        }
        return argTypes;
    }

    private Constructor<?> findConstructor(Class<?> type, Class<?>[] argTypes)
            throws NoSuchMethodException {
        if (argTypes == null)
            return type.getConstructor();
        for (Constructor<?> constructor : type.getConstructors()) {
            if (matches(constructor, argTypes)) {
                return constructor;
            }
        }
        StringBuilder msg = new StringBuilder();
        msg.append(type).append(" ").append(Arrays.toString(argTypes)).append(". Only know:");
        for (Constructor<?> constructor : type.getConstructors()) {
            msg.append("\n    ").append(constructor);
        }
        throw new NoSuchMethodException(msg.toString());
    }

    private boolean matches(Constructor<?> constructor, Class<?>[] argTypes) {
        Type[] constructorArgs = constructor.getGenericParameterTypes();
        if (constructorArgs.length != argTypes.length)
            return false;
        for (int i = 0; i < constructorArgs.length; i++) {
            Class<?> constructorArgType = classOf(constructorArgs[i]);
            Class<?> methodArgType = argTypes[i];
            if (matches(constructorArgType, methodArgType)) {
                return true;
            }
        }
        return false;
    }

    private Class<?> classOf(Type type) {
        if (type instanceof Class<?>)
            return (Class<?>) type;
        if (type instanceof ParameterizedType)
            return classOf(((ParameterizedType) type).getRawType());
        throw new IllegalArgumentException("don't know how to get class out of a "
                + type.getClass().getName());
    }

    private boolean matches(Class<?> constructorArgType, Class<?> methodArgType) {
        if (methodArgType == null && !constructorArgType.isPrimitive()) {
            return true;
        }
        if (constructorArgType.isAssignableFrom(methodArgType)) {
            return true;
        }
        if (constructorArgType.isPrimitive()) {
            if (constructorArgType == Boolean.TYPE && methodArgType == Boolean.class)
                return true;
            if (constructorArgType == Byte.TYPE && methodArgType == Byte.class)
                return true;
            if (constructorArgType == Character.TYPE && methodArgType == Character.class)
                return true;
            if (constructorArgType == Short.TYPE && methodArgType == Short.class)
                return true;
            if (constructorArgType == Integer.TYPE && methodArgType == Integer.class)
                return true;
            if (constructorArgType == Long.TYPE && methodArgType == Long.class)
                return true;
            if (constructorArgType == Float.TYPE && methodArgType == Float.class)
                return true;
            if (constructorArgType == Double.TYPE && methodArgType == Double.class)
                return true;
            throw new AssertionError("unexpected primitive");
        }
        return false;
    }
}
