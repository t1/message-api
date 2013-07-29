package net.java.messageapi.adapter;

import java.lang.reflect.*;
import java.util.Arrays;

/**
 * Creates instances of the class corresponding to a method invocation.
 */
public class MessageCallFactory<T> {

    private final Method method;
    private final Class<T> pojoClass;

    public MessageCallFactory(Method method) {
        this.method = method;
        this.pojoClass = getType();
    }

    @SuppressWarnings("unchecked")
    private Class<T> getType() {
        return (Class<T>) new MethodAsClassGenerator(method).get();
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

    private Constructor<?> findConstructor(Class<?> type, Class<?>[] argTypes) throws NoSuchMethodException {
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
        throw new IllegalArgumentException("don't know how to get class out of a " + type.getClass().getName());
    }

    private boolean matches(Class<?> constructorArgType, Class<?> methodArgType) {
        if (methodArgType == null && !constructorArgType.isPrimitive()) {
            return true;
        }
        if (constructorArgType.isAssignableFrom(methodArgType)) {
            return true;
        }
        if (constructorArgType.isPrimitive()) {
            if (constructorArgType == boolean.class && methodArgType == Boolean.class)
                return true;
            if (constructorArgType == byte.class && methodArgType == Byte.class)
                return true;
            if (constructorArgType == char.class && methodArgType == Character.class)
                return true;
            if (constructorArgType == short.class && methodArgType == Short.class)
                return true;
            if (constructorArgType == int.class && methodArgType == Integer.class)
                return true;
            if (constructorArgType == long.class && methodArgType == Long.class)
                return true;
            if (constructorArgType == float.class && methodArgType == Float.class)
                return true;
            if (constructorArgType == double.class && methodArgType == Double.class)
                return true;
            throw new AssertionError("unexpected primitive");
        }
        return false;
    }
}
