package net.java.messageapi.processor;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

public class MdbGenerator extends AbstractGenerator {
    public MdbGenerator(Messager messager, Filer filer) {
        super(messager, filer);
    }

    @Override
    public void process(Element element) {
        TypeElement type = (TypeElement) element;
        String mdbName = type.getQualifiedName() + "MDB";
        note("Generating " + mdbName);
    }
}
