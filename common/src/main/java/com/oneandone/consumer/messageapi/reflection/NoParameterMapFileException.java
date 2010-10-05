package com.oneandone.consumer.messageapi.reflection;

import java.lang.reflect.Method;

/**
 * There is no <code>.parametermap</code> file to retrieve the parameter names from.
 * 
 * @see Parameter
 */
public class NoParameterMapFileException extends ParameterMapFileException {
    private static final long serialVersionUID = 1L;

    public NoParameterMapFileException(Method method, String message) {
        super(method, message);
    }
}