package com.oneandone.consumer.messageapi.processor;

import java.util.Set;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.xml.bind.annotation.XmlRootElement;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SupportedAnnotationClasses(XmlRootElement.class)
public class XmlRootElementAnnotationProcessor extends AbstractProcessor2 {

    private JaxbIndexGenerator generator;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        generator = new JaxbIndexGenerator(getMessager(), processingEnv.getFiler());
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        generator.process(roundEnv.getElementsAnnotatedWith(XmlRootElement.class));

        if (roundEnv.processingOver()) {
            generator.finish();
        }

        return false;
    }
}
