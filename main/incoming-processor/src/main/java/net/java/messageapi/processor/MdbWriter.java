package net.java.messageapi.processor;

import javax.lang.model.element.*;

import net.java.messageapi.DestinationName;

public abstract class MdbWriter {
    private final TypeElement messageType;
    private final String mdbTypeName;
    private final String pkg;

    public MdbWriter(Element element) {
        this.messageType = (TypeElement) element;
        this.mdbTypeName = getTypeName(element);
        this.pkg = getPackageName(element);
    }

    private String getTypeName(Element element) {
        StringBuilder result = new StringBuilder(element.getSimpleName().toString());
        for (Element e = element.getEnclosingElement(); e instanceof TypeElement; e = e.getEnclosingElement()) {
            result.insert(0, '$').insert(0, e.getSimpleName());
        }
        result.append(getSuffix());
        return result.toString();
    }

    private String getPackageName(Element element) {
        for (Element e = element.getEnclosingElement(); e != null; e = e.getEnclosingElement()) {
            if (e instanceof PackageElement) {
                return ((PackageElement) e).getQualifiedName().toString();
            }
        }
        throw new RuntimeException("no package found for " + element);
    }

    public String getFileName() {
        return pkg + '.' + mdbTypeName;
    }

    protected abstract String getSuffix();

    public String generate() {
        String fqcn = messageType.getQualifiedName().toString();
        String simple = messageType.getSimpleName().toString();
        String destination = getDestinationName();

        // TODO add messageSelector on the version

        StringBuilder source = new StringBuilder();
        source.append("package ").append(pkg).append(";\n");
        source.append("\n");
        source.append("import javax.ejb.ActivationConfigProperty;\n");
        source.append("import javax.ejb.MessageDriven;\n");
        additionalImports(source);
        source.append("import javax.inject.Inject;\n");
        source.append("import javax.jms.MessageListener;\n");
        source.append("\n");
        source.append("import ").append(fqcn).append(";\n");
        source.append("import net.java.messageapi.JmsIncoming;\n");
        source.append("import net.java.messageapi.adapter.").append(getDecoderClass()).append(";\n");
        source.append("\n");
        source.append("@MessageDriven(messageListenerInterface = MessageListener.class, //\n");
        source.append("activationConfig = { @ActivationConfigProperty(\n");
        source.append("propertyName = \"destination\", propertyValue = \"").append(destination).append("\") })\n");
        source.append("public class ").append(mdbTypeName).append(" extends ") //
        .append(getDecoderClass()).append("<").append(simple).append("> {\n");
        source.append("    public ").append(mdbTypeName).append("() {\n");
        source.append("        super(null, null);\n");
        source.append("        throw new UnsupportedOperationException(\n");
        source.append("                \"default consturctor required by MDB lifecycle, but never called\");\n");
        source.append("    }\n");
        source.append("\n");
        source.append("    @Inject\n");
        source.append("    public ").append(mdbTypeName).append("(@JmsIncoming ");
        appendInjectedType(source, simple);
        source.append(" payload) {\n");
        source.append("        super(").append(simple).append(".class, payload);\n").append("    }\n");
        source.append("}\n");
        return source.toString();
    }

    protected void additionalImports(@SuppressWarnings("unused") StringBuilder source) {
        // do nothing by default
    }

    protected abstract String getDecoderClass();

    protected void appendInjectedType(StringBuilder source, String simple) {
        source.append(simple);
    }

    private String getDestinationName() {
        DestinationName destinationNameAnnotation = messageType.getAnnotation(DestinationName.class);
        if (destinationNameAnnotation == null)
            return messageType.getQualifiedName().toString();
        return destinationNameAnnotation.value();
    }

    @Override
    public String toString() {
        return getFileName();
    }
}
