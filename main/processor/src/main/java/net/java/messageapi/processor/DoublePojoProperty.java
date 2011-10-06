package net.java.messageapi.processor;

import java.io.IOException;
import java.io.Writer;

class DoublePojoProperty extends AbstractPrimitivePojoProperty {

    public DoublePojoProperty(String type, String name) {
        super(type, name);
    }

    @Override
    protected String getDefaultValue() {
        return "0d";
    }

    @Override
    protected void writeHashCodeValueTo(Writer writer) throws IOException {
        String temp = "Double.doubleToLongBits(" + name + ")";
        writer.append("(int) (");
        writer.append(temp);
        writer.append(" ^ (");
        writer.append(temp);
        writer.append(" >>> 32))");
    }
}
