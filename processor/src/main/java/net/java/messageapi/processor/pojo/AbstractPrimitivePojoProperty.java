package net.java.messageapi.processor.pojo;

import java.io.IOException;
import java.io.Writer;

/**
 * @see PojoProperty
 */
abstract class AbstractPrimitivePojoProperty extends PojoProperty {

    public AbstractPrimitivePojoProperty(String type, String name) {
        super(type, name);
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
