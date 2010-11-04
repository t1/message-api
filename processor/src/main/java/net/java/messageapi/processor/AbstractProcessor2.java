package net.java.messageapi.processor;

import java.lang.annotation.Annotation;
import java.util.Set;

import javax.annotation.processing.*;

import com.google.common.base.Function;
import com.google.common.collect.*;

/**
 * Extends the {@link AbstractProcessor} with the handling for the
 * {@link SupportedAnnotationClasses} annotation and replaces the {@link Messager} when running with
 * Maven.
 */
abstract class AbstractProcessor2 extends AbstractProcessor {

    private Messager messager;

    /**
     * This is the poor man's constructor that the designers of the apt processor API chose, so they
     * can rely on a no-arg constructor and then call this method. I'd have preferred the convention
     * to simply be a constructor with the env argument.
     * <p>
     * We use it to replace the {@link Messager} with a {@link StandardOutMessager} when we see that
     * we are running in maven.
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        boolean isMaven = System.getProperty("maven.home") != null;
        messager = isMaven ? new ThrowOnErrorMessager(new StandardOutMessager())
                : processingEnv.getMessager();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        SupportedAnnotationClasses supported = this.getClass().getAnnotation(
                SupportedAnnotationClasses.class);
        if (supported == null)
            return super.getSupportedAnnotationTypes();

        return Sets.newHashSet(Lists.transform(ImmutableList.of(supported.value()),
                new Function<Class<? extends Annotation>, String>() {
                    @Override
                    public String apply(Class<? extends Annotation> from) {
                        return from.getName();
                    }
                }));
    }

    public Messager getMessager() {
        return messager;
    }
}