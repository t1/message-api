package net.java.messageapi.processor;

import java.util.Set;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic.Kind;

import net.java.messageapi.*;

/**
 * Annotation processor that generates message POJOs for all methods in an interface annotated as {@link MessageApi}.
 */
@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SupportedAnnotationClasses({ MessageApi.class, MessageEvent.class })
public class MessageApiAnnotationProcessor extends AbstractProcessor2 {

    private PojoGenerator pojoGenerator;
    private ParameterMapGenerator propertyNameIndexGenerator;
    private MessageEventMdbGenerator mdbGenerator;

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);
        Messager messager = getMessager();
        Filer filer = env.getFiler();
        Elements utils = env.getElementUtils();
        pojoGenerator = new PojoGenerator(messager, filer, utils);
        propertyNameIndexGenerator = new ParameterMapGenerator(messager, filer, utils);
        mdbGenerator = new MessageEventMdbGenerator(messager, filer, utils);
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
        for (Element messageEvent : roundEnv.getElementsAnnotatedWith(MessageEvent.class)) {
            try {
                mdbGenerator.process(messageEvent);
            } catch (Error e) {
                getMessager().printMessage(Kind.ERROR, "Error while processing MessageEvent: " + e, messageEvent);
            } catch (RuntimeException e) {
                getMessager().printMessage(Kind.ERROR, "can't process MessageEvent: " + e, messageEvent);
            }
        }
        return false;
    }
}
