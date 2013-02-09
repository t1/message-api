package net.java.messageapi.reflection;

public class FallbackParameterNameSupplier implements ParameterNameSupplier {
    @Override
    public String get(Parameter parameter) {
        // TODO use type name instead of "arg"; and if it's unique even without a suffix
        return "arg" + parameter.getIndex();
    }
}
