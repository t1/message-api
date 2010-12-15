package net.java.messageapi.converter;

public class IdentityConverter<BoundType> extends Converter<BoundType> {

    @Override
    public String marshal(BoundType v) throws Exception {
        return v.toString();
    }

    @Override
    @SuppressWarnings("unchecked")
    public BoundType unmarshal(String v) throws Exception {
        return (BoundType) v;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
