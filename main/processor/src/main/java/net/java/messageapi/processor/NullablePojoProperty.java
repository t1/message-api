package net.java.messageapi.processor;

import java.io.IOException;
import java.io.Writer;

abstract class NullablePojoProperty extends PojoProperty {

    public NullablePojoProperty(Pojo pojo, String type, String name) {
        super(pojo, type, name);
    }

    @Override
    protected String getDefaultValue() {
        return "null";
    }

    @Override
    public void writeHashCodeTo(Writer writer) throws IOException {
        writer.append("\t\tresult = prime * result + ");
        writeHashCodeCallTo(writer);
        writer.append(";\n");
    }

    protected abstract void writeHashCodeCallTo(Writer writer) throws IOException;

    @Override
    public void writeEqualsTo(Writer writer) throws IOException {
        writer.append("\t\tif (").append(name).append(" == null) {\n");
        writer.append("\t\t\tif (other.").append(name).append(" != null)\n");
        writer.append("\t\t\t\treturn false;\n");
        writer.append("\t\t} else if (!");
        writeEqualsCompareTo(writer);
        writer.append(")\n");
        writer.append("\t\t\treturn false;\n");
    }

    protected abstract void writeEqualsCompareTo(Writer writer) throws IOException;
}
