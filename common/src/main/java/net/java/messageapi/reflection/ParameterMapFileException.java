package net.java.messageapi.reflection;

import java.lang.reflect.Method;

public abstract class ParameterMapFileException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final Method method;

    public ParameterMapFileException(Method method, String message) {
        super(message);
        this.method = method;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + "\n\tfor: " + method.getDeclaringClass().getName() + "#"
                + method.getName();
    }
}