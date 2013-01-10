package net.java.messageapi.processor;

import java.io.*;

import javax.annotation.processing.*;
import javax.lang.model.element.Element;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileObject;

import net.java.messageapi.MessageEvent;

public class MessageEventMdbGenerator extends AbstractGenerator {
    public MessageEventMdbGenerator(Messager messager, Filer filer, Elements utils) {
        super(messager, filer, utils);
    }

    @Override
    public void process(Element element) {
        MessageEvent messageEvent = element.getAnnotation(MessageEvent.class);
        if (messageEvent == null) {
            error("no MessageEvent annotation on target element", element);
            return;
        }
        if (messageEvent.generateMdb() == false) {
            note("message event has generateMdb parameter set to false --> skip", element);
            return;
        }

        MessageEventMdbWriter writer = new MessageEventMdbWriter(element);

        String fileName = writer.getFileName();
        note("Generating " + fileName);

        String mdbSource = writer.generate();
        write(mdbSource, fileName, element);
    }

    private void write(String mdbSource, String fileName, Element type) {
        Writer writer = null;
        try {
            JavaFileObject sourceFile = createSourceFile(fileName, type);
            writer = sourceFile.openWriter();
            writer.write(mdbSource);
        } catch (IOException e) {
            error("Can't write MDB\n" + e, type);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }
}