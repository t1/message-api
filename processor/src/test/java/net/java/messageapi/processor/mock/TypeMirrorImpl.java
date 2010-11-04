package net.java.messageapi.processor.mock;

import javax.lang.model.type.*;

class TypeMirrorImpl implements TypeMirror {

    private final Class<?> type;

    public TypeMirrorImpl(Class<?> type) {
        this.type = type;
    }

    @Override
    public <R, P> R accept(TypeVisitor<R, P> v, P p) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TypeKind getKind() {
        if (Boolean.TYPE == type)
            return TypeKind.BOOLEAN;
        if (Character.TYPE == type)
            return TypeKind.CHAR;
        if (Byte.TYPE == type)
            return TypeKind.BYTE;
        if (Short.TYPE == type)
            return TypeKind.SHORT;
        if (Integer.TYPE == type)
            return TypeKind.INT;
        if (Long.TYPE == type)
            return TypeKind.LONG;
        if (Float.TYPE == type)
            return TypeKind.FLOAT;
        if (Double.TYPE == type)
            return TypeKind.DOUBLE;
        if (Void.TYPE == type)
            return TypeKind.VOID;
        if (type.isArray())
            return TypeKind.ARRAY;
        return TypeKind.DECLARED;
    }

    @Override
    public String toString() {
        if (type.isArray())
            return type.getComponentType().getName() + "[]";
        return type.getName();
    }
}
