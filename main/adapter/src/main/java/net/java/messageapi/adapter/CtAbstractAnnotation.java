package net.java.messageapi.adapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.*;

/**
 * This class should normally be part of javassist. But because CtField and CtClass don't share a
 * common interface with the methods to access the {@link ConstPool}, and get and add attributes, we
 * have to resort to subclasses.
 */
public abstract class CtAbstractAnnotation {
    protected static final String VISIBLE = AnnotationsAttribute.visibleTag;

    protected ConstPool constPool;
    protected Annotation annotation;

    protected void init(@SuppressWarnings("hiding") ConstPool constPool,
            Class<? extends java.lang.annotation.Annotation> type) {
        this.constPool = constPool;
        this.annotation = new Annotation(type.getName(), constPool);
    }

    protected void init(java.lang.annotation.Annotation property) {
        try {
            copyFieldsOrThrow(property);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private void copyFieldsOrThrow(java.lang.annotation.Annotation property)
            throws IllegalAccessException, InvocationTargetException {
        for (Method method : property.annotationType().getMethods()) {
            if (method.getDeclaringClass() == Object.class
                    || method.getDeclaringClass() == java.lang.annotation.Annotation.class)
                continue;
            Object value = method.invoke(property);
            addMemberValue(method.getName(), (Boolean) value);
        }
    }

    public void addMemberValue(String name, MemberValue value) {
        annotation.addMemberValue(name, value);
    }

    public void addMemberValue(String name, Annotation value) {
        addMemberValue(name, new AnnotationMemberValue(value, constPool));
    }

    public void addMemberValue(String name, boolean value) {
        addMemberValue(name, new BooleanMemberValue(value, constPool));
    }

    public void addMemberValue(String name, Class<?> value) {
        addMemberValue(name, new ClassMemberValue(value.getName(), constPool));
    }

    public void addMemberValue(String name, String value) {
        addMemberValue(name, new StringMemberValue(value, constPool));
    }

    public void addMemberValue(String name, List<String> value) {
        addMemberValue(name, getArrayMember(value));
    }

    private ArrayMemberValue getArrayMember(List<String> valueList) {
        MemberValue[] elements = new MemberValue[valueList.size()];
        for (int i = 0; i < elements.length; i++) {
            String propertyName = valueList.get(i);
            elements[i] = new StringMemberValue(propertyName, constPool);
        }
        ArrayMemberValue result = new ArrayMemberValue(constPool);
        result.setValue(elements);
        return result;
    }

    /** This method would normally go into CtField/CtClass */
    public void set() {
        AnnotationsAttribute annotationsAttribute = getOrCreateAnnotationsAttribute();
        annotationsAttribute.addAnnotation(annotation);
        addAttribute(annotationsAttribute);
    }

    private AnnotationsAttribute getOrCreateAnnotationsAttribute() {
        AnnotationsAttribute result = getAnnotationsAttribute();
        if (result == null) {
            result = new AnnotationsAttribute(constPool, VISIBLE);
        }
        return result;
    }

    protected abstract AnnotationsAttribute getAnnotationsAttribute();

    protected abstract void addAttribute(AnnotationsAttribute annotationsAttribute);
}
