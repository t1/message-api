package net.java.messageapi.processor;

import java.util.List;
import java.util.Set;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;

import net.java.messageapi.MessageApi;

import com.google.common.annotations.VisibleForTesting;

/**
 * Annotation processor that generates message POJOs for all methods in an interface annotated as
 * {@link MessageApi}.
 */
@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SupportedAnnotationClasses(MessageApi.class)
public class MessageApiAnnotationProcessor extends AbstractProcessor2 {

    private PojoGenerator pojoGenerator;
    private ParameterMapGenerator propertyNameIndexGenerator;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        Messager messager = getMessager();
        Filer filer = processingEnv.getFiler();
        pojoGenerator = new PojoGenerator(messager, filer);
        propertyNameIndexGenerator = new ParameterMapGenerator(messager, filer);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element messageApi : roundEnv.getElementsAnnotatedWith(MessageApi.class)) {
            try {
                pojoGenerator.process(messageApi);
                propertyNameIndexGenerator.process(messageApi);
            } catch (Error e) {
                getMessager().printMessage(Kind.ERROR, "Error while processing MessageApi: " + e,
                        messageApi);
            } catch (RuntimeException e) {
                getMessager().printMessage(Kind.ERROR, "can't process MessageApi: " + e, messageApi);
            }
        }
        return true;
    }

    @VisibleForTesting
    public List<Pojo> getGeneratedPojos() {
        return pojoGenerator.getGeneratedPojos();
    }
}
