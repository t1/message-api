package net.java.messageapi.adapter;

import java.lang.annotation.Annotation;
import java.util.*;

import javax.enterprise.inject.spi.AnnotatedType;

public class AnnotatedTypeAnnotationsWrapper<X> extends AnnotatedTypeWrapper<X> {

    private final Set<Annotation> annotations;

    public AnnotatedTypeAnnotationsWrapper(AnnotatedType<X> target, Annotation annotation) {
        super(target);
        Set<Annotation> result = new HashSet<Annotation>();
        result.addAll(super.getAnnotations());
        result.add(annotation);
        this.annotations = Collections.unmodifiableSet(result);
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
