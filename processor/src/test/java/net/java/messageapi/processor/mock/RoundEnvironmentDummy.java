/**
 * 
 */
package net.java.messageapi.processor.mock;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import com.google.common.collect.*;

class RoundEnvironmentDummy implements RoundEnvironment {
    private final Set<Element> rootElements = Sets.newHashSet();
    private final Multimap<Class<? extends Annotation>, Element> annotationMap = HashMultimap.create();
    private boolean processingOver;

    private boolean errorRaised;

    @Override
    public boolean processingOver() {
        return processingOver;
    }

    @Override
    public Set<? extends Element> getRootElements() {
        return Collections.unmodifiableSet(rootElements);
    }

    @Override
    public Set<? extends Element> getElementsAnnotatedWith(Class<? extends Annotation> a) {
        return Collections.unmodifiableSet((Set<? extends Element>) annotationMap.get(a));
    }

    @Override
    public Set<? extends Element> getElementsAnnotatedWith(TypeElement a) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean errorRaised() {
        return errorRaised;
    }

    public void add(Class<?> type) {
        TypeElement typeElement = new TypeElementImpl(type);
        rootElements.add(typeElement);
        for (Annotation annotation : type.getAnnotations()) {
            annotationMap.put(annotation.annotationType(), typeElement);
        }
    }

    @Override
    public String toString() {
        return "[errorRaised=" + errorRaised + ", annotationMap=" + annotationMap
                + ", processingOver=" + processingOver + "]";
    }

    void setProcessingOver(boolean processingOver) {
        this.processingOver = processingOver;
    }
}