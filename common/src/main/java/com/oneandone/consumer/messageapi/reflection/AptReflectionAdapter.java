/**
 * 
 */
package com.oneandone.consumer.messageapi.reflection;

import static com.google.common.base.Predicates.*;
import static com.google.common.collect.Iterables.*;

import java.util.List;

import javax.lang.model.element.*;

public class AptReflectionAdapter extends ReflectionAdapter<ExecutableElement> {

    public static PackageElement getPackageOf(Element startElement) {
        for (Element e = startElement; e != null; e = e.getEnclosingElement()) {
            if (e.getKind() == ElementKind.PACKAGE) {
                return (PackageElement) e;
            }
        }
        throw new IllegalStateException("no package for " + startElement);
    }

    public AptReflectionAdapter(ExecutableElement method) {
        super(method);
    }

    @Override
    protected String getMethodName(ExecutableElement method) {
        return method.getSimpleName().toString();
    }

    @Override
    protected Iterable<ExecutableElement> siblings(ExecutableElement method) {
        Iterable<? extends Element> allSiblings = method.getEnclosingElement().getEnclosedElements();
        @SuppressWarnings("unchecked")
        Iterable<ExecutableElement> siblingMethods = (Iterable<ExecutableElement>) filter(
                allSiblings, instanceOf(ExecutableElement.class));
        return siblingMethods;
    }

    @Override
    public String getPackage() {
        return getPackageOf(method).getQualifiedName().toString();
    }

    @Override
    protected List<?> getParameters() {
        return method.getParameters();
    }

    @Override
    protected String typeNameOf(Object object) {
        return ((Element) object).asType().toString();
    }
}