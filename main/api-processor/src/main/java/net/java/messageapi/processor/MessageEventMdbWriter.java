package net.java.messageapi.processor;

import javax.lang.model.element.*;

import net.java.messageapi.DestinationName;

public class MessageEventMdbWriter {
    private final TypeElement messageType;

    public MessageEventMdbWriter(Element element) {
        this.messageType = (TypeElement) element;
    }

    public String generate() {
        String pkg = getPackageName();
        String mdbTypeName = getMdbTypeName();
        String simple = getSimpleTypeName();
        String destination = getDestinationName();

        // TODO add messageSelector on the version

        StringBuilder source = new StringBuilder();
        source.append("package ").append(pkg).append(";\n");
        source.append("\n");
        source.append("import javax.ejb.ActivationConfigProperty;\n");
        source.append("import javax.ejb.MessageDriven;\n");
        source.append("import javax.enterprise.event.Event;\n");
        source.append("import javax.inject.Inject;\n");
        source.append("import javax.jms.MessageListener;\n");
        source.append("\n");
        source.append("import net.java.messageapi.JmsIncoming;\n");
        source.append("import net.java.messageapi.adapter.").append("EventDecoder").append(";\n");
        source.append("\n");
        source.append("@MessageDriven(messageListenerInterface = MessageListener.class, //\n");
        source.append("activationConfig = { @ActivationConfigProperty(\n");
        source.append("propertyName = \"destination\", propertyValue = \"").append(destination).append("\") })\n");
        source.append("public class ").append(mdbTypeName).append(" extends ") //
        .append("EventDecoder").append("<").append(simple).append("> {\n");
        source.append("    public ").append(mdbTypeName).append("() {\n");
        source.append("        super(null, null);\n");
        source.append("        throw new UnsupportedOperationException(\n");
        source.append("                \"default consturctor required by MDB lifecycle, but never called\");\n");
        source.append("    }\n");
        source.append("\n");
        source.append("    @Inject\n");
        source.append("    public ").append(mdbTypeName).append("(@JmsIncoming ");
        source.append("Event<");
        source.append(simple);
        source.append(">");
        source.append(" payload) {\n");
        source.append("        super(").append(simple).append(".class, payload);\n").append("    }\n");
        source.append("}\n");
        return source.toString();
    }

    private String getPackageName() {
        for (Element e = messageType.getEnclosingElement(); e != null; e = e.getEnclosingElement()) {
            if (e instanceof PackageElement) {
                return ((PackageElement) e).getQualifiedName().toString();
            }
        }
        throw new RuntimeException("no package found for " + messageType);
    }

    private String getMdbTypeName() {
        StringBuilder result = new StringBuilder(getSimpleTypeName());
        for (Element e = messageType.getEnclosingElement(); e instanceof TypeElement; e = e.getEnclosingElement()) {
            result.insert(0, '$').insert(0, e.getSimpleName());
        }
        result.append("$MDB");
        return result.toString();
    }

    private String getSimpleTypeName() {
        return messageType.getSimpleName().toString();
    }

    private String getDestinationName() {
        DestinationName destinationNameAnnotation = messageType.getAnnotation(DestinationName.class);
        if (destinationNameAnnotation == null)
            return messageType.getQualifiedName().toString();
        return destinationNameAnnotation.value();
    }

    public String getFileName() {
        return getPackageName() + '.' + getMdbTypeName();
    }
}