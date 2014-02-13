package net.java.messageapi.reflection;

import net.java.messageapi.JmsName;

public class JmsNameSupplier implements ParameterNameSupplier {

    private final ParameterNameSupplier delegate;

    public JmsNameSupplier(ParameterNameSupplier delegate) {
        this.delegate = delegate;
    }

    @Override
    public String get(Parameter parameter) {
        if (parameter.isAnnotationPresent(JmsName.class))
            return parameter.getAnnotation(JmsName.class).value();
        return delegate.get(parameter);
    }
}
