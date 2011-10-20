package net.java.messageapi.pojo;

import java.io.IOException;
import java.io.Writer;

class OtherPrimitivePojoProperty extends AbstractPrimitivePojoProperty {

    public OtherPrimitivePojoProperty(Pojo pojo, String type, String name) {
        super(pojo, type, name);
    }

    @Override
    protected String getDefaultValue() {
        return "0";
    }

    @Override
    protected void writeHashCodeValueTo(Writer writer) throws IOException {
        writer.append(name);
    }

    @Override
    public void writeEqualsTo(Writer writer) throws IOException {
        writer.append("\t\tif (").append(name).append(" != other.").append(name).append(")\n");
        writer.append("\t\t\treturn false;\n");
    }
}
