package net.java.messageapi.processor;

import javax.lang.model.element.Element;

public class EventMdbWriter extends MdbWriter {
    public EventMdbWriter(Element element) {
        super(element);
    }

    @Override
    public String getSuffix() {
        return "$MDB";
    }

    @Override
    public String getDecoderClass() {
        return "EventDecoder";
    }

    @Override
    protected void appendInjectedType(StringBuilder source, String simple) {
        source.append("Event<");
        super.appendInjectedType(source, simple);
        source.append(">");
    }

    @Override
    protected void additionalImports(StringBuilder source) {
        source.append("import javax.enterprise.event.Event;\n");
    }
}
