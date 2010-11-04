package net.java.messageapi.processor.pojo;

import java.io.IOException;
import java.io.Writer;

/**
 * @see PojoProperty
 */
class FloatPojoProperty extends AbstractPrimitivePojoProperty {

    public FloatPojoProperty(String type, String name) {
        super(type, name);
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
