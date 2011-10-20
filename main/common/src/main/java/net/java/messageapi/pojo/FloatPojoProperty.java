package net.java.messageapi.pojo;

import java.io.IOException;
import java.io.Writer;

class FloatPojoProperty extends AbstractPrimitivePojoProperty {

    public FloatPojoProperty(Pojo pojo, String type, String name) {
        super(pojo, type, name);
    }

    @Override
    protected String getDefaultValue() {
        return "0f";
    }

    @Override
    protected void writeHashCodeValueTo(Writer writer) throws IOException {
        writer.append("Float.floatToIntBits(");
        writer.append(name);
        writer.append(")");
    }
}
