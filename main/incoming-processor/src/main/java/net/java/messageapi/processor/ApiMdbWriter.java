package net.java.messageapi.processor;

import javax.lang.model.element.Element;

public class ApiMdbWriter extends MdbWriter {
    public ApiMdbWriter(Element element) {
        super(element);
    }

    @Override
    public String getSuffix() {
        return "MDB";
    }

    @Override
    public String getDecoderClass() {
        return "MessageDecoder";
    }
}
