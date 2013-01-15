package net.java.messageapi.processor.mock;

import java.lang.annotation.Annotation;
import java.util.*;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.*;

class RoundEnvironmentDummy implements RoundEnvironment {
    private final Set<Element> rootElements = new HashSet<Element>();
    private final Map<Class<? extends Annotation>, Set<Element>> annotationMap = new HashMap<Class<? extends Annotation>, Set<Element>>();
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
        return Collections.unmodifiableSet(annotationMap.get(a));
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
            Set<Element> set = annotationMap.get(annotation.annotationType());
            if (set == null) {
                set = new HashSet<Element>();
                annotationMap.put(annotation.annotationType(), set);
            }
            set.add(typeElement);
        }
    }

    @Override
    public String toString() {
        return "[errorRaised=" + errorRaised + ", annotationMap=" + annotationMap + ", processingOver="
                + processingOver + "]";
    }

    void setProcessingOver(boolean processingOver) {
        this.processingOver = processingOver;
    }
}