package net.java.messageapi.adapter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.*;

import javax.enterprise.context.Dependent;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.util.AnnotationLiteral;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class MessageApiBean<T> implements Bean<T> {
    private final Logger log = LoggerFactory.getLogger(MessageApiBean.class);

    public static <T> MessageApiBean<T> of(Class<T> api, InjectionPoint injectionPoint) {
        return new MessageApiBean<T>(api, injectionPoint);
    }

    private final Class<T> api;
    private final InjectionPoint injectionPoint;

    private MessageApiBean(Class<T> api, InjectionPoint injectionPoint) {
        this.api = api;
        this.injectionPoint = injectionPoint;
    }

    @Override
    public Class<?> getBeanClass() {
        return api;
    }

    @Override
    public Set<InjectionPoint> getInjectionPoints() {
        return Collections.emptySet();
    }

    @Override
    public String getName() {
        return api.getSimpleName();
    }

    @Override
    public Set<Annotation> getQualifiers() {
        Set<Annotation> qualifiers = new HashSet<Annotation>();
        qualifiers.add(new AnnotationLiteral<Default>() {
            private static final long serialVersionUID = 1L;
        });
        qualifiers.add(new AnnotationLiteral<Any>() {
            private static final long serialVersionUID = 1L;
        });
        return qualifiers;
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return Dependent.class;
    }

    @Override
    public Set<Class<? extends Annotation>> getStereotypes() {
        return Collections.emptySet();
    }

    @Override
    public Set<Type> getTypes() {
        Set<Type> types = new HashSet<Type>();
        types.add(api);
        types.add(Object.class);
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

    @Override
    public T create(CreationalContext<T> ctx) {
        log.info("================ create {} for {}", api, injectionPoint);
        log.info("annotations: {}", injectionPoint.getAnnotated().getAnnotations());
        // TODO handle annotations
        return MessageSender.of(api);
    }

    @Override
    public void destroy(T instance, CreationalContext<T> ctx) {
        ctx.release();
    }
}