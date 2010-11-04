package net.java.messageapi.processor.mock;

import java.util.EnumSet;
import java.util.Set;

import javax.lang.model.element.Modifier;

class AbstractElementImpl {

    public AbstractElementImpl() {
        super();
    }

    protected Set<Modifier> convertModifiers(int modifiers) {
        EnumSet<Modifier> result = EnumSet.noneOf(Modifier.class);
        if (java.lang.reflect.Modifier.isAbstract(modifiers))
            result.add(Modifier.ABSTRACT);
        if (java.lang.reflect.Modifier.isFinal(modifiers))
            result.add(Modifier.FINAL);
        if (java.lang.reflect.Modifier.isNative(modifiers))
            result.add(Modifier.NATIVE);
        if (java.lang.reflect.Modifier.isPrivate(modifiers))
            result.add(Modifier.PRIVATE);
        if (java.lang.reflect.Modifier.isProtected(modifiers))
            result.add(Modifier.PROTECTED);
        if (java.lang.reflect.Modifier.isPublic(modifiers))
            result.add(Modifier.PUBLIC);
        if (java.lang.reflect.Modifier.isStatic(modifiers))
            result.add(Modifier.STATIC);
        if (java.lang.reflect.Modifier.isStrict(modifiers))
            result.add(Modifier.STRICTFP);
        if (java.lang.reflect.Modifier.isSynchronized(modifiers))
            result.add(Modifier.SYNCHRONIZED);
        if (java.lang.reflect.Modifier.isTransient(modifiers))
            result.add(Modifier.TRANSIENT);
        if (java.lang.reflect.Modifier.isVolatile(modifiers))
            result.add(Modifier.VOLATILE);
        return result;
    }

}