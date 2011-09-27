package net.java.messageapi.adapter;

import java.lang.annotation.Annotation;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

/** A CDI bean is identified by its type and qualifiers */
class BeanId {
    final Class<?> type;
    final Set<Annotation> qualifiers;

    BeanId(Class<?> type, Set<Annotation> qualifiers) {
        this.type = type;
        this.qualifiers = ImmutableSet.copyOf(qualifiers);
    }

    @Override
    public String toString() {
        return type.getSimpleName() + qualifiers;
    }

    @Override
    public int hashCode() {
        return qualifiers.hashCode() * 31 + type.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        BeanId that = (BeanId) obj;
        return qualifiers.equals(that.qualifiers) && type.equals(that.type);
    }
}