package net.java.messageapi.processor.mock;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;

public class MethodElementImpl extends AbstractElementImpl implements ExecutableElement {

    private final Method method;

    public MethodElementImpl(Method method) {
        if (method == null)
            throw new NullPointerException();
        this.method = method;
    }

    @Override
    public AnnotationValue getDefaultValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<? extends VariableElement> getParameters() {
        List<VariableElement> result = new ArrayList<VariableElement>();
        int n = method.getParameterTypes().length;
        for (int i = 0; i < n; i++) {
            result.add(new ParameterElementImpl(this, method, i));
        }
        return result;
    }

    @Override
    public TypeMirror getReturnType() {
        return new TypeMirrorImpl(method.getReturnType());
    }

    @Override
    public List<? extends TypeMirror> getThrownTypes() {
        List<TypeMirror> result = new ArrayList<TypeMirror>();
        for (Class<?> exceptionType : method.getExceptionTypes()) {
            result.add(new TypeMirrorImpl(exceptionType));
        }
        return result;
    }

    @Override
    public List<? extends TypeParameterElement> getTypeParameters() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isVarArgs() {
        return method.isVarArgs();
    }

    @Override
    public <R, P> R accept(ElementVisitor<R, P> v, P p) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TypeMirror asType() {
        throw new UnsupportedOperationException();
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
        return new TypeElementImpl(method.getDeclaringClass());
    }

    @Override
    public ElementKind getKind() {
        return ElementKind.METHOD;
    }

    @Override
    public Set<Modifier> getModifiers() {
        return convertModifiers(method.getModifiers());
    }

    @Override
    public Name getSimpleName() {
        return new NameImpl(method.getName());
    }

    @Override
    public String toString() {
        return method.toString();
    }

    @Override
    public int hashCode() {
        return method.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MethodElementImpl that = (MethodElementImpl) obj;
        return this.method.equals(that.method);
    }
}
