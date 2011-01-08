package net.java.messageapi.adapter;

import java.lang.reflect.Type;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

class TypeClosureBuilder {
    private final ImmutableSet.Builder<Type> builder = ImmutableSet.builder();

    public TypeClosureBuilder(Class<?> type) {
        builder.add(Object.class);
        addType(type);
    }

    private void addType(Class<?> type) {
        builder.add(type);
        addInterfaces(type);
        addSuperType(type);
    }

    private void addInterfaces(Class<?> type) {
        for (Class<?> i : type.getInterfaces()) {
            addType(i);
        }
    }

    private void addSuperType(Class<?> type) {
        Class<?> superType = type.getSuperclass();
        if (superType != null) {
            addType(superType);
        }
    }

    public Set<Type> get() {
        return builder.build();
    }
}