package net.java.messageapi.reflection;

import java.lang.reflect.Method;

public interface ParameterNameSupplier {
    String get(Method method, int index);
}
