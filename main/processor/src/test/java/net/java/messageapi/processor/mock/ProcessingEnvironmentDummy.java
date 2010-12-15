package net.java.messageapi.processor.mock;

import java.lang.annotation.Annotation;
import java.util.*;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class ProcessingEnvironmentDummy implements ProcessingEnvironment {

    private final Elements elementUtils;
    private final Messager messager;
    private final Filer filer;
    private final int nrOfRounds;

    private Locale locale;
    private Map<String, String> options;
    private SourceVersion sourceVersion;
    private Types typeUtils;

    public ProcessingEnvironmentDummy(Messager messager) {
        this(messager, new FilerDummy(), 1);
    }

    public ProcessingEnvironmentDummy(Messager messager, Filer filer) {
        this(messager, filer, 1);
    }

    public ProcessingEnvironmentDummy(Messager messager, Filer filer, int nrOfRounds) {
        this.messager = messager;
        this.filer = filer;
        this.nrOfRounds = nrOfRounds;
        this.elementUtils = new ElementUtilDummy();
    }

    @Override
    public Elements getElementUtils() {
        return elementUtils;
    }

    @Override
    public Filer getFiler() {
        return filer;
    }

    @Override
    public Locale getLocale() {
        return locale;
    }

    @Override
    public Messager getMessager() {
        return messager;
    }

    @Override
    public Map<String, String> getOptions() {
        return options;
    }

    @Override
    public SourceVersion getSourceVersion() {
        return sourceVersion;
    }

    @Override
    public Types getTypeUtils() {
        return typeUtils;
    }

    public void process(Processor processor, Class<?>... processedTypes) {
        processor.init(this);

        RoundEnvironmentDummy roundEnv = new RoundEnvironmentDummy();
        Set<TypeElement> annotations = Sets.newLinkedHashSet();
        for (Class<?> processedType : processedTypes) {
            roundEnv.add(processedType);
            annotations.addAll(getAnnotations(processedType, processor));
        }
        for (int i = 1; i < nrOfRounds; i++)
            processor.process(annotations, roundEnv);

        // the final round
        roundEnv.setProcessingOver(true);
        processor.process(annotations, roundEnv);

    }

    private ImmutableSet<TypeElement> getAnnotations(Class<?> type, Processor processor) {
        Set<String> supportedAnnotations = processor.getSupportedAnnotationTypes();

        ImmutableSet.Builder<TypeElement> result = ImmutableSet.builder();
        for (Annotation definedAnnotation : getAnnotationsOf(type)) {
            if (supportedAnnotations.contains(definedAnnotation.annotationType().getName())) {
                result.add(new TypeElementImpl(definedAnnotation.annotationType()));
            }
        }
        return result.build();
    }

    private List<Annotation> getAnnotationsOf(Class<?> type) {
        ImmutableList.Builder<Annotation> result = ImmutableList.builder();
        addAnnotations(result, type);
        return result.build();
    }

    private void addAnnotations(ImmutableList.Builder<Annotation> result, Class<?> type) {
        if (type == null)
            return;
        for (Annotation annotation : type.getAnnotations()) {
            result.add(annotation);
        }
        for (Class<?> interf : type.getInterfaces()) {
            addAnnotations(result, interf);
        }
        addAnnotations(result, type.getSuperclass());
    }
}
