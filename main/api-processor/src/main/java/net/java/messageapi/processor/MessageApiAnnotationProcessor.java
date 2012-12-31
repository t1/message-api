package net.java.messageapi.processor;

import java.util.Set;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic.Kind;

import net.java.messageapi.MessageApi;

/**
 * Annotation processor that generates message POJOs for all methods in an interface annotated as {@link MessageApi}.
 */
@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SupportedAnnotationClasses(MessageApi.class)
public class MessageApiAnnotationProcessor extends AbstractProcessor2 {

    private PojoGenerator pojoGenerator;
    private ParameterMapGenerator propertyNameIndexGenerator;

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);
        Messager messager = getMessager();
        Filer filer = env.getFiler();
        Elements utils = env.getElementUtils();
        pojoGenerator = new PojoGenerator(messager, filer, utils);
        propertyNameIndexGenerator = new ParameterMapGenerator(messager, filer, utils);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element messageApi : roundEnv.getElementsAnnotatedWith(MessageApi.class)) {
            try {
                pojoGenerator.process(messageApi);
                propertyNameIndexGenerator.process(messageApi);
            } catch (Error e) {
                getMessager().printMessage(Kind.ERROR, "Error while processing MessageApi: " + e, messageApi);
            } catch (RuntimeException e) {
                getMessager().printMessage(Kind.ERROR, "can't process MessageApi: " + e, messageApi);
            }
        }
        return false;
    }
}
