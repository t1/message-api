package net.java.messageapi.converter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class StringToBooleanConverter extends Converter<Boolean> {

    @XmlAttribute(name = "true")
    private final String symbolTrue;
    @XmlAttribute(name = "false")
    private final String symbolFalse;

    public StringToBooleanConverter() {
        this("false", "true");
    }

    public StringToBooleanConverter(String symbolTrue, String symbolFalse) {
        this.symbolFalse = symbolFalse;
        this.symbolTrue = symbolTrue;
    }

    @Override
    public String marshal(Boolean v) throws Exception {
        return v ? symbolTrue : symbolFalse;
    }

    @Override
    public Boolean unmarshal(String v) throws Exception {
        if (symbolFalse.equals(v))
            return false;
        if (symbolTrue.equals(v))
            return true;
        throw new IllegalArgumentException("[" + v + "] must be " + symbolFalse + " or "
                + symbolTrue + " to convert to bool");
    }

    @Override
    public String toString() {
        return super.toString() + "[" + symbolTrue + "|" + symbolFalse + "]";
    }
}
