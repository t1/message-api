package net.java.messageapi.pojo;

import java.io.IOException;
import java.io.Writer;

abstract class AbstractPrimitivePojoProperty extends PojoProperty {

    public AbstractPrimitivePojoProperty(Pojo pojo, String type, String name) {
        super(pojo, type, name);
    }

    @Override
    public void writeHashCodeTo(Writer writer) throws IOException {
        writer.append("\t\tresult = prime * result + ");
        writeHashCodeValueTo(writer);
        writer.append(";\n");
    }

    protected abstract void writeHashCodeValueTo(Writer writer) throws IOException;

    @Override
    public void writeEqualsTo(Writer writer) throws IOException {
        writer.append("\t\tif (").append(name).append(" != other.").append(name).append(")\n");
        writer.append("\t\t\treturn false;\n");
    }
}
