package net.java.messageapi.adapter.javassist;

import javassist.CtField;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.FieldInfo;

/** @see CtAbstractAnnotation */
public class CtFieldAnnotation extends CtAbstractAnnotation {
    private final FieldInfo fieldInfo;

    public CtFieldAnnotation(CtField field, Class<? extends java.lang.annotation.Annotation> type) {
        this.fieldInfo = field.getFieldInfo();
        init(fieldInfo.getConstPool(), type);
    }

    public CtFieldAnnotation(CtField field, java.lang.annotation.Annotation property) {
        this(field, property.annotationType());
        init(property);
    }

    @Override
    protected void addAttribute(AnnotationsAttribute annotationsAttribute) {
        fieldInfo.addAttribute(annotationsAttribute);
    }

    @Override
    protected AnnotationsAttribute getAnnotationsAttribute() {
        return (AnnotationsAttribute) fieldInfo.getAttribute(VISIBLE);
    }
}
