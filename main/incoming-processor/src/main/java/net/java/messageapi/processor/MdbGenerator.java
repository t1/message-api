package net.java.messageapi.processor;

import java.io.IOException;
import java.io.Writer;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.tools.JavaFileObject;

import net.java.messageapi.MessageApi;
import net.java.messageapi.MessageEvent;

public class MdbGenerator extends AbstractGenerator {
    public MdbGenerator(Messager messager, ProcessingEnvironment env) {
        super(messager, env);
    }

    @Override
    public void process(Element element) {
        MdbWriter writer = writerFor(element);

        note("Generating " + writer);

        String mdbSource = writer.generate();
        String fileName = writer.getFileName();

        write(mdbSource, fileName, element);
    }

    public MdbWriter writerFor(Element type) {
        MessageApi messageApi = type.getAnnotation(MessageApi.class);
        if (messageApi != null)
            return new ApiMdbWriter(type);
        MessageEvent messageEvent = type.getAnnotation(MessageEvent.class);
        if (messageEvent != null)
            return new EventMdbWriter(type);
        throw new RuntimeException("can't determine MDB writer for " + type);
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
