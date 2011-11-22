package net.java.messageapi.processor;

import java.util.Set;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.xml.bind.annotation.XmlRootElement;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SupportedAnnotationClasses(XmlRootElement.class)
public class XmlRootElementAnnotationProcessor extends AbstractProcessor2 {

    private JaxbIndexGenerator generator;

    @Override
    public synchronized void init(@SuppressWarnings("hiding") ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        generator = new JaxbIndexGenerator(getMessager(), processingEnv.getFiler());
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(XmlRootElement.class)) {
            generator.process(element);
        }

        if (roundEnv.processingOver()) {
            generator.finish();
        }

        return false;
    }
}
