package net.java.messageapi.processor;

import javax.annotation.processing.Messager;
import javax.lang.model.element.*;
import javax.tools.Diagnostic.Kind;

public class ThrowOnErrorMessager implements Messager {

    private final Messager target;

    public ThrowOnErrorMessager(Messager target) {
        this.target = target;
    }

    private void throwOnError(Kind kind, CharSequence msg) {
        if (kind == Kind.ERROR) {
            throw new RuntimeException(msg.toString());
        }
    }

    @Override
    public void printMessage(Kind kind, CharSequence msg) {
        target.printMessage(kind, msg);
        throwOnError(kind, msg);
    }

    @Override
    public void printMessage(Kind kind, CharSequence msg, Element e) {
        target.printMessage(kind, msg, e);
        throwOnError(kind, msg);
    }

    @Override
    public void printMessage(Kind kind, CharSequence msg, Element e, AnnotationMirror a) {
        target.printMessage(kind, msg, e, a);
        throwOnError(kind, msg);
    }

    @Override
    public void printMessage(Kind kind, CharSequence msg, Element e, AnnotationMirror a,
            AnnotationValue v) {
        target.printMessage(kind, msg, e, a, v);
        throwOnError(kind, msg);
    }
}
