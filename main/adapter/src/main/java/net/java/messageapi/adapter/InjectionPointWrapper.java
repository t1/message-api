package net.java.messageapi.adapter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.lang.reflect.Type;
import java.util.Set;

import javax.enterprise.inject.spi.*;

public class InjectionPointWrapper implements InjectionPoint {

    private final InjectionPoint target;

    public InjectionPointWrapper(InjectionPoint target) {
        this.target = target;
    }

    @Override
    public Annotated getAnnotated() {
        return target.getAnnotated();
    }

    @Override
    public Bean<?> getBean() {
        return target.getBean();
    }

    @Override
    public Member getMember() {
        return target.getMember();
    }

    @Override
    public Set<Annotation> getQualifiers() {
        return target.getQualifiers();
    }

    @Override
    public Type getType() {
        return target.getType();
    }

    @Override
    public boolean isDelegate() {
        return target.isDelegate();
    }

    @Override
    public boolean isTransient() {
        return target.isTransient();
    }
}
