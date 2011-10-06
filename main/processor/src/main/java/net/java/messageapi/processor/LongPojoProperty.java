package net.java.messageapi.processor;

import java.io.IOException;
import java.io.Writer;

class LongPojoProperty extends AbstractPrimitivePojoProperty {

    public LongPojoProperty(String type, String name) {
        super(type, name);
    }

    @Override
    protected String getDefaultValue() {
        return "0L";
    }

    @Override
    protected void writeHashCodeValueTo(Writer writer) throws IOException {
        writer.append("(int) (");
        writer.append(name);
        writer.append(" ^ (");
        writer.append(name);
        writer.append(" >>> 32))");
    }
}
