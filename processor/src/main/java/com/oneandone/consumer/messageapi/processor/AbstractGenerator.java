package com.oneandone.consumer.messageapi.processor;

import java.io.IOException;
import java.util.List;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.*;
import javax.tools.Diagnostic.Kind;

import com.oneandone.consumer.messageapi.reflection.AptReflectionAdapter;

public class AbstractGenerator {

    private final Messager messager;
    private final Filer filer;

    public AbstractGenerator(Messager messager, Filer filer) {
        this.messager = messager;
        this.filer = filer;
    }

    protected void error(CharSequence message) {
        messager.printMessage(Kind.ERROR, message);
    }

    protected void error(CharSequence message, Element element) {
        messager.printMessage(Kind.ERROR, message, element);
    }

    protected void warn(CharSequence message) {
        messager.printMessage(Kind.WARNING, message);
    }

    protected void warn(CharSequence message, Element element) {
        messager.printMessage(Kind.WARNING, message, element);
    }

    protected void note(CharSequence message) {
        messager.printMessage(Kind.NOTE, message);
    }

    protected JavaFileObject createSourceFile(String name, Element... elements) throws IOException {
        return filer.createSourceFile(name, elements);
    }

    protected FileObject createResourceFile(String pkg, String name, List<TypeElement> rootElements)
            throws IOException {
        Element[] elements = toArray(rootElements);
        return filer.createResource(StandardLocation.CLASS_OUTPUT, pkg, name, elements);
    }

    private Element[] toArray(List<TypeElement> rootElements) {
        return rootElements.toArray(new Element[rootElements.size()]);
    }

    protected String getCompoundName(TypeElement typeElement) {
        String packageName = getPackageOf(typeElement);
        String qualifiedName = typeElement.getQualifiedName().toString();
        return qualifiedName.substring(packageName.length() + 1);
    }

    protected String getPackageOf(Element element) {
        return AptReflectionAdapter.getPackageOf(element).getQualifiedName().toString();
    }
}