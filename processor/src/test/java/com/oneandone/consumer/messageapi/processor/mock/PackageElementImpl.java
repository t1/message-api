package com.oneandone.consumer.messageapi.processor.mock;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;

class PackageElementImpl implements PackageElement {

    private final Package pkg;

    public PackageElementImpl(Package pkg) {
        if (pkg == null)
            throw new NullPointerException();
        this.pkg = pkg;
    }

    @Override
    public Name getQualifiedName() {
        return new NameImpl(pkg.getName());
    }

    @Override
    public boolean isUnnamed() {
        throw new UnsupportedOperationException();
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
        throw new UnsupportedOperationException();
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
        return null;
    }

    @Override
    public ElementKind getKind() {
        return ElementKind.PACKAGE;
    }

    @Override
    public Set<Modifier> getModifiers() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Name getSimpleName() {
        return getQualifiedName();
    }

    @Override
    public String toString() {
        return pkg.toString();
    }

    @Override
    public int hashCode() {
        return pkg.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PackageElementImpl other = (PackageElementImpl) obj;
        return pkg.getName().equals(other.pkg.getName());
    }
}
