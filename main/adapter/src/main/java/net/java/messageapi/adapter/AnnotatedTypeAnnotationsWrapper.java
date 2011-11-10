package net.java.messageapi.adapter;

import java.lang.annotation.Annotation;
import java.util.Set;

import javax.enterprise.inject.spi.AnnotatedType;

import com.google.common.collect.ImmutableSet;

public class AnnotatedTypeAnnotationsWrapper<X> extends AnnotatedTypeWrapper<X> {

    private final ImmutableSet<Annotation> annotations;

    public AnnotatedTypeAnnotationsWrapper(AnnotatedType<X> target, Annotation annotation) {
        super(target);
        ImmutableSet.Builder<Annotation> builder = ImmutableSet.builder();
        builder.addAll(super.getAnnotations());
        builder.add(annotation);
        this.annotations = builder.build();
    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> type) {
        for (Annotation annotation : annotations) {
            if (type.isInstance(annotation)) {
                return type.cast(annotation);
            }
        }
        return null;
    }

    @Override
    public Set<Annotation> getAnnotations() {
        return annotations;
    }

    @Override
    public boolean isAnnotationPresent(Class<? extends Annotation> type) {
        return getAnnotation(type) != null;
    }
}
