package net.java.messageapi.processor;

import java.util.Set;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;

import net.java.messageapi.MessageApi;
import net.java.messageapi.MessageEvent;

/**
 * Annotation processor that generates message MDBs for all {@link MessageApi}s and {@link MessageEvent}s.
 * 
 * TODO extract shared code between this and the API annotation processor
 */
@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SupportedAnnotationClasses({ MessageApi.class, MessageEvent.class })
public class MessageApiIncomingAnnotationProcessor extends AbstractProcessor2 {

    private MdbGenerator mdbGenerator;

    @Override
    public synchronized void init(@SuppressWarnings("hiding") ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        Messager messager = getMessager();
        Filer filer = processingEnv.getFiler();
        mdbGenerator = new MdbGenerator(messager, filer);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element messageApi : roundEnv.getElementsAnnotatedWith(MessageApi.class)) {
            try {
                mdbGenerator.process(messageApi);
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
