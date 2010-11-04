package net.java.messageapi.reflection;

import java.lang.reflect.Method;

/**
 * The <code>.parametermap</code> file is not valid.
 * 
 * @see Parameter
 */
public class InvalidParameterMapFileException extends ParameterMapFileException {
    private static final long serialVersionUID = 1L;

    public InvalidParameterMapFileException(Method method, String message) {
        super(method, message);
    }
}