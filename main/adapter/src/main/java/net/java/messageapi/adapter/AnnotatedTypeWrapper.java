package net.java.messageapi.adapter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;

import javax.enterprise.inject.spi.*;

public class AnnotatedTypeWrapper<T> implements AnnotatedType<T> {

    private final AnnotatedType<T> target;

    public AnnotatedTypeWrapper(AnnotatedType<T> target) {
        this.target = target;
    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> type) {
        return target.getAnnotation(type);
    }

    @Override
    public Set<Annotation> getAnnotations() {
        return target.getAnnotations();
    }

    @Override
    public Type getBaseType() {
        return target.getBaseType();
    }

    @Override
    public Set<Type> getTypeClosure() {
        return target.getTypeClosure();
    }

    @Override
    public boolean isAnnotationPresent(Class<? extends Annotation> type) {
        return target.isAnnotationPresent(type);
    }

    @Override
    public Set<AnnotatedConstructor<T>> getConstructors() {
        return target.getConstructors();
    }

    @Override
    public Set<AnnotatedField<? super T>> getFields() {
        return target.getFields();
    }

    @Override
    public Class<T> getJavaClass() {
        return target.getJavaClass();
    }

    @Override
    public Set<AnnotatedMethod<? super T>> getMethods() {
        return target.getMethods();
    }
}
