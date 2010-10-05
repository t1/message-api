package com.oneandone.consumer.messageapi.processor.mock;

import javax.annotation.processing.Messager;
import javax.lang.model.element.*;
import javax.tools.Diagnostic.Kind;

public class PrintMessager implements Messager {

    @Override
    public void printMessage(Kind kind, CharSequence msg) {
        System.out.println(kind + ": " + msg);
    }

    @Override
    public void printMessage(Kind kind, CharSequence msg, Element e) {
        System.out.println(kind + ": " + msg + ": " + e);
    }

    @Override
    public void printMessage(Kind kind, CharSequence msg, Element e, AnnotationMirror a) {
        System.out.println(kind + ": " + msg + ": " + e + ": " + a);
    }

    @Override
    public void printMessage(Kind kind, CharSequence msg, Element e, AnnotationMirror a,
            AnnotationValue v) {
        System.out.println(kind + ": " + msg + ": " + e + ": " + a + ": " + v);
    }
}
