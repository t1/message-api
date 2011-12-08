package net.java.messageapi.processor.mock;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;

import com.google.common.collect.Lists;

public class TypeElementImpl extends AbstractElementImpl implements TypeElement {

    private final Class<?> type;

    public TypeElementImpl(Class<?> type) {
        if (type == null)
            throw new NullPointerException();
        this.type = type;
    }

    @Override
    public List<? extends TypeMirror> getInterfaces() {
        throw new UnsupportedOperationException();
    }

    @Override
    public NestingKind getNestingKind() {
        if (type.isAnonymousClass())
            return NestingKind.ANONYMOUS;
        if (type.isLocalClass())
            return NestingKind.LOCAL;
        if (type.isMemberClass())
            return NestingKind.MEMBER;
        return NestingKind.TOP_LEVEL;
    }

    @Override
    public Name getQualifiedName() {
        return new NameImpl(type.getName());
    }

    @Override
    public TypeMirror getSuperclass() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<? extends TypeParameterElement> getTypeParameters() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <R, P> R accept(ElementVisitor<R, P> v, P p) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TypeMirror asType() {
        return new TypeMirrorImpl(type);
    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
        return type.getAnnotation(annotationType);
    }

    @Override
    public List<? extends AnnotationMirror> getAnnotationMirrors() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<? extends Element> getEnclosedElements() {
        if (!type.isInterface())
            throw new UnsupportedOperationException();
        List<Element> result = Lists.newArrayList();
        for (Method method : type.getMethods()) {
            result.add(new MethodElementImpl(method));
        }
        return result;
    }

    @Override
    public Element getEnclosingElement() {
        Class<?> enclosingClass = type.getEnclosingClass();
        if (enclosingClass != null)
            return new TypeElementImpl(enclosingClass);
        return new PackageElementImpl(type.getPackage());
    }

    @Override
    public ElementKind getKind() {
        if (type.isEnum())
            return ElementKind.ENUM;
        if (type.isInterface())
            return ElementKind.INTERFACE;
        return ElementKind.CLASS;
    }

    @Override
    public Set<Modifier> getModifiers() {
        return convertModifiers(type.getModifiers());
    }

    @Override
    public Name getSimpleName() {
        return new NameImpl(type.getSimpleName());
    }

    @Override
    public String toString() {
        return type.toString();
    }

    @Override
    public int hashCode() {
        return type.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TypeElementImpl that = (TypeElementImpl) obj;
        return this.type.equals(that.type);
    }
}
