package net.java.messageapi.reflection;

import java.lang.reflect.Method;

public class FallbackParameterNameSupplier implements ParameterNameSupplier {
    @Override
    public String get(Method method, int index) {
        return "arg" + index;
    }
}
