package net.java.messageapi.processor;

import java.io.IOException;
import java.io.Writer;

class ArrayPojoProperty extends NullablePojoProperty {

    public ArrayPojoProperty(Pojo pojo, String type, String name) {
        super(pojo, type, name);
    }

    @Override
    protected void writeHashCodeCallTo(Writer writer) throws IOException {
        writer.append("java.util.Arrays.hashCode(").append(name).append(")");
    }

    @Override
    protected void writeEqualsCompareTo(Writer writer) throws IOException {
        writer.append("java.util.Arrays.equals(").append(name);
        writer.append(", other.").append(name).append(")");
    }

    @Override
    public void writeToStringTo(Writer writer) throws IOException {
        writer.append("java.util.Arrays.toString(").append(name).append(")");
    }
}
