package net.java.messageapi.converter;

public class StringToBooleanConverter extends Converter<Boolean> {

    private final String symbolTrue;
    private final String symbolFalse;

    public StringToBooleanConverter(String symbolFalse, String symbolTrue) {
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

}
