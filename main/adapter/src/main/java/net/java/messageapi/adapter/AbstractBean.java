package net.java.messageapi.adapter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.util.AnnotationLiteral;

import com.google.common.collect.ImmutableSet;

/**
 * Bean implementation with useful defaults... you only have to override
 * {@link Bean#create(CreationalContext)}.
 */
public abstract class AbstractBean<T> implements Bean<T> {

    private final Class<T> beanClass;
    private final Class<? extends Annotation> scope;
    private final Set<Type> types;

    public AbstractBean(Class<T> beanClass, Class<? extends Annotation> scope) {
        this.beanClass = beanClass;
        this.scope = scope;
        this.types = new TypeClosureBuilder(beanClass).get();
    }

    @Override
    public void destroy(T arg0, CreationalContext<T> arg1) {
    }

    @Override
    public Class<?> getBeanClass() {
        return beanClass;
    }

    @Override
    public Set<InjectionPoint> getInjectionPoints() {
        return Collections.emptySet();
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    @SuppressWarnings("serial")
    public Set<Annotation> getQualifiers() {
        return ImmutableSet.<Annotation> of(//
                new AnnotationLiteral<Default>() {
                }, new AnnotationLiteral<Any>() {
                });
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return scope;
    }

    @Override
    public Set<Class<? extends Annotation>> getStereotypes() {
        return Collections.emptySet();
    }

    @Override
    public Set<Type> getTypes() {
        return types;
    }

    @Override
    public boolean isAlternative() {
        return false;
    }

    @Override
    public boolean isNullable() {
        return false;
    }
}