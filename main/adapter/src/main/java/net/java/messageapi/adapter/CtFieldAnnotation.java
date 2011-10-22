package net.java.messageapi.adapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javassist.CtField;
import javassist.bytecode.*;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.BooleanMemberValue;

public class CtFieldAnnotation {
    private static final String VISIBLE = AnnotationsAttribute.visibleTag;

    private final CtField field;
    private final Annotation annotation;

    public CtFieldAnnotation(CtField field, Class<? extends java.lang.annotation.Annotation> type) {
        this.field = field;
        annotation = new Annotation(type.getName(), getConstPool());
    }

    public CtFieldAnnotation(CtField field, java.lang.annotation.Annotation property) {
        this(field, property.annotationType());

        try {
            copyFields(property);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private void copyFields(java.lang.annotation.Annotation property)
            throws IllegalAccessException, InvocationTargetException {
        for (Method method : property.annotationType().getMethods()) {
            if (method.getDeclaringClass() == Object.class
                    || method.getDeclaringClass() == java.lang.annotation.Annotation.class)
                continue;
            Object value = method.invoke(property);
            addMemberValue(method.getName(), (Boolean) value);
        }
    }

    private ConstPool getConstPool() {
        return field.getFieldInfo().getConstPool();
    }

    public void addMemberValue(String name, boolean value) {
        ConstPool constPool = getConstPool();
        annotation.addMemberValue(name, new BooleanMemberValue(value, constPool));
    }

    /** This method would normally go into CtField */
    public void set() {
        FieldInfo fieldInfo = field.getFieldInfo();
        AnnotationsAttribute annotationsAttribute = (AnnotationsAttribute) fieldInfo.getAttribute(VISIBLE);
        boolean isNew = annotationsAttribute == null;
        if (isNew) {
            annotationsAttribute = new AnnotationsAttribute(getConstPool(), VISIBLE);
        }
        annotationsAttribute.addAnnotation(annotation);
        fieldInfo.addAttribute(annotationsAttribute);
    }
}
