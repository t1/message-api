package com.oneandone.consumer.messageapi.processor.mock;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;

class ParameterElementImpl implements VariableElement {

    private final ExecutableElement container;
    private final Method method;
    private final int index;

    public ParameterElementImpl(ExecutableElement container, Method method, int index) {
        if (container == null)
            throw new NullPointerException();
        this.container = container;
        if (method == null)
            throw new NullPointerException();
        this.method = method;
        if (index < 0 || index >= method.getParameterTypes().length)
            throw new IllegalArgumentException(Integer.toString(index));
        this.index = index;
    }

    @Override
    public Object getConstantValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <R, P> R accept(ElementVisitor<R, P> v, P p) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TypeMirror asType() {
        return new TypeMirrorImpl(method.getParameterTypes()[index]);
    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
        return method.getAnnotation(annotationType);
    }

    @Override
    public List<? extends AnnotationMirror> getAnnotationMirrors() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<? extends Element> getEnclosedElements() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Element getEnclosingElement() {
        return container;
    }

    @Override
    public ElementKind getKind() {
        return ElementKind.PARAMETER;
    }

    @Override
    public Set<Modifier> getModifiers() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Name getSimpleName() {
        return new NameImpl("arg" + index);
    }

    @Override
    public String toString() {
        return method.getParameterTypes()[index].getCanonicalName();
    }
}
