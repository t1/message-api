package net.java.messageapi.processor;

import java.io.IOException;
import java.util.List;

import javax.annotation.processing.*;
import javax.lang.model.element.*;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic.Kind;
import javax.tools.*;

abstract class AbstractGenerator {

    private final Messager messager;
    private final Filer filer;
    private final Elements utils;

    public AbstractGenerator(Messager messager, Filer filer, Elements utils) {
        this.messager = messager;
        this.filer = filer;
        this.utils = utils;
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

    protected void note(CharSequence message, Element element) {
        messager.printMessage(Kind.NOTE, message, element);
    }

    protected JavaFileObject createSourceFile(String name, Element... elements) throws IOException {
        return filer.createSourceFile(name, elements);
    }

    protected FileObject createResourceFile(String pkg, String name, List<TypeElement> rootElements) throws IOException {
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
        return utils.getPackageOf(element).getQualifiedName().toString();
    }

    public abstract void process(Element element);
}