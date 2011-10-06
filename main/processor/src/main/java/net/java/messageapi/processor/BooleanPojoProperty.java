package net.java.messageapi.processor;

import java.io.IOException;
import java.io.Writer;

class BooleanPojoProperty extends AbstractPrimitivePojoProperty {

    public BooleanPojoProperty(String type, String name) {
        super(type, name);
    }

    @Override
    protected String getDefaultValue() {
        return "false";
    }

    @Override
    protected void writeHashCodeValueTo(Writer writer) throws IOException {
        writer.append("(");
        writer.append(name);
        writer.append(" ? 1231 : 1237)");
    }
}
