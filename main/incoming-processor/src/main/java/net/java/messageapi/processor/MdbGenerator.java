package net.java.messageapi.processor;

import java.io.IOException;
import java.io.Writer;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;

import net.java.messageapi.*;

public class MdbGenerator extends AbstractGenerator {
    private enum AdapterType {
        API {
            @Override
            public String getSuffix() {
                return "MDB";
            }

            @Override
            public String getDecoderClass() {
                return "MessageDecoder";
            }
        },
        EVENT {
            @Override
            public String getSuffix() {
                return "$MDB";
            }

            @Override
            public String getDecoderClass() {
                return "EventDecoder";
            }
        };

        public static AdapterType of(TypeElement type) {
            MessageApi messageApi = type.getAnnotation(MessageApi.class);
            if (messageApi != null)
                return AdapterType.API;
            MessageEvent messageEvent = type.getAnnotation(MessageEvent.class);
            if (messageEvent != null)
                return AdapterType.EVENT;
            throw new RuntimeException("can't determine AdapterType for " + type);
        }

        public abstract String getSuffix();

        public abstract String getDecoderClass();
    }

    public MdbGenerator(Messager messager, Filer filer) {
        super(messager, filer);
    }

    @Override
    public void process(Element element) {
        TypeElement type = (TypeElement) element;
        AdapterType adapterType = AdapterType.of(type);
        String fileName = type.getQualifiedName() + adapterType.getSuffix();
        note("Generating " + fileName);

        String mdbSource = generate(type, adapterType);
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

    private String generate(TypeElement type, AdapterType adapterType) {
        String fqcn = type.getQualifiedName().toString();
        String simple = type.getSimpleName().toString();
        String pkg = getPackageOf(type);
        String destination = getDestinationName(type);
        String mdbName = simple + adapterType.getSuffix();

        // TODO add messageSelector on the version

        StringBuilder source = new StringBuilder();
        source.append("package ").append(pkg).append(";\n");
        source.append("\n");
        source.append("import javax.ejb.ActivationConfigProperty;\n");
        source.append("import javax.ejb.MessageDriven;\n");
        if (adapterType == AdapterType.EVENT)
            source.append("import javax.enterprise.event.Event;\n");
        source.append("import javax.inject.Inject;\n");
        source.append("import javax.jms.MessageListener;\n");
        source.append("\n");
        source.append("import ").append(fqcn).append(";\n");
        source.append("import net.java.messageapi.JmsIncoming;\n");
        source.append("import net.java.messageapi.adapter.").append(adapterType.getDecoderClass()).append(";\n");
        source.append("\n");
        source.append("@MessageDriven(messageListenerInterface = MessageListener.class, //\n");
        source.append("activationConfig = { @ActivationConfigProperty(\n");
        source.append("propertyName = \"destination\", propertyValue = \"").append(destination).append("\") })\n");
        source.append("public class ").append(mdbName).append(" extends ") //
        .append(adapterType.getDecoderClass()).append("<").append(simple).append("> {\n");
        source.append("    public ").append(mdbName).append("() {\n");
        source.append("        super(null, null);\n");
        source.append("        throw new UnsupportedOperationException(\n");
        source.append("                \"default consturctor required by MDB lifecycle, but never called\");\n");
        source.append("    }\n");
        source.append("\n");
        source.append("    @Inject\n");
        source.append("    public ").append(mdbName).append("(@JmsIncoming ");
        if (adapterType == AdapterType.EVENT)
            source.append("Event<");
        source.append(simple);
        if (adapterType == AdapterType.EVENT)
            source.append(">");
        source.append(" payload) {\n");
        source.append("        super(").append(simple).append(".class, payload);\n").append("    }\n");
        source.append("}\n");
        return source.toString();
    }

    private String getDestinationName(TypeElement type) {
        DestinationName destinationNameAnnotation = type.getAnnotation(DestinationName.class);
        if (destinationNameAnnotation == null)
            return type.getQualifiedName().toString();
        return destinationNameAnnotation.value();
    }
}
