package net.java.messageapi.pojo;

import java.io.IOException;
import java.io.Writer;

class LongPojoProperty extends AbstractPrimitivePojoProperty {

    public LongPojoProperty(Pojo pojo, String type, String name) {
        super(pojo, type, name);
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
