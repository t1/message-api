package net.java.messageapi.processor;

import java.util.Set;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;
import javax.xml.bind.annotation.XmlRootElement;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SupportedAnnotationClasses(XmlRootElement.class)
public class XmlRootElementAnnotationProcessor extends AbstractProcessor2 {

    private JaxbIndexGenerator generator;

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);
        generator = new JaxbIndexGenerator(getMessager(), env.getFiler(), env.getElementUtils());
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(XmlRootElement.class)) {
            if (element.getAnnotation(XmlRootElement.class) == null) {
                String message = "element " + element
                        + " is probably annotated with an annotation not on the classpath";
                getMessager().printMessage(Kind.WARNING, message, element);
            } else {
                generator.process(element);
            }
        }

        if (roundEnv.processingOver()) {
            generator.finish();
        }

        return false;
    }
}
