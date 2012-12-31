package net.java.messageapi.processor;

import javax.annotation.processing.Messager;
import javax.lang.model.element.*;
import javax.tools.Diagnostic.Kind;

/**
 * Prints messages to standard out
 */
public class StandardOutMessager implements Messager {

    @Override
    public void printMessage(Kind kind, CharSequence msg) {
        System.out.println("[" + kind + "] " + msg);
    }

    @Override
    public void printMessage(Kind kind, CharSequence msg, Element e) {
        System.out.println("[" + kind + "] " + msg + " [" + info(e) + "]");
    }

    @Override
    public void printMessage(Kind kind, CharSequence msg, Element e, AnnotationMirror a) {
        System.out.println("[" + kind + "] " + msg + " [" + info(e) + "][" + a + "]");
    }

    @Override
    public void printMessage(Kind kind, CharSequence msg, Element e, AnnotationMirror a, AnnotationValue v) {
        System.out.println("[" + kind + "] " + msg + " [" + info(e) + "][" + a + "][" + v + "]");
    }

    private String info(Element element) {
        switch (element.getKind()) {
        case CONSTRUCTOR:
        case FIELD:
        case INSTANCE_INIT:
        case LOCAL_VARIABLE:
        case METHOD:
        case PARAMETER:
        case STATIC_INIT:
            return element + "@" + element.getEnclosingElement();
        default:
            return element.toString();
        }
    }
}
