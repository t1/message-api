package net.java.messageapi.processor;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.*;

/**
 * Extends the {@link AbstractProcessor} with the handling for the {@link SupportedAnnotationClasses} annotation and
 * replaces the {@link Messager} when running with Maven.
 */
abstract public class AbstractProcessor2 extends AbstractProcessor {

    private Messager messager;

    /**
     * This is the poor man's constructor that the designers of the apt processor API chose, so they can rely on a
     * no-arg constructor and then call this method. I'd have preferred the convention to simply be a constructor with
     * the env argument.
     * <p>
     * We use it to replace the {@link Messager} with a {@link StandardOutMessager} when we see that we are running in
     * maven.
     */
    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);
        boolean isMaven = System.getProperty("maven.home") != null;
        this.messager = isMaven ? new ThrowOnErrorMessager(new StandardOutMessager()) : env.getMessager();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        SupportedAnnotationClasses supported = this.getClass().getAnnotation(SupportedAnnotationClasses.class);
        if (supported == null)
            return super.getSupportedAnnotationTypes();

        Set<String> result = new HashSet<String>();
        for (Class<? extends Annotation> annotation : supported.value()) {
            result.add(annotation.getName());
        }
        return result;
    }

    public Messager getMessager() {
        return messager;
    }
}
