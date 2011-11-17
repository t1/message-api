package net.java.messageapi.reflection;

import java.lang.reflect.Method;

public class FallbackParameterNameSupplier implements ParameterNameSupplier {
    @Override
    public String get(Method method, int index) {
        // TODO use type name instead of "arg"; and if it's unique even without a suffix
        return "arg" + index;
    }
}
