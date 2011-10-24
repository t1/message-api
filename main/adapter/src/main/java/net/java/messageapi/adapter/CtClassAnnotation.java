package net.java.messageapi.adapter;

import javassist.CtClass;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;

/** @see CtAbstractAnnotation */
public class CtClassAnnotation extends CtAbstractAnnotation {
    private final ClassFile classFile;

    public CtClassAnnotation(CtClass ctClass, Class<? extends java.lang.annotation.Annotation> type) {
        this.classFile = ctClass.getClassFile();
        init(classFile.getConstPool(), type);
    }

    public CtClassAnnotation(CtClass ctClass, java.lang.annotation.Annotation property) {
        this(ctClass, property.annotationType());
        init(property);
    }

    @Override
    protected void addAttribute(AnnotationsAttribute annotationsAttribute) {
        classFile.addAttribute(annotationsAttribute);
    }

    @Override
    protected AnnotationsAttribute getAnnotationsAttribute() {
        return (AnnotationsAttribute) classFile.getAttribute(VISIBLE);
    }
}
