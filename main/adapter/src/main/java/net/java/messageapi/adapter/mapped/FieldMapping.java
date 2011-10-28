package net.java.messageapi.adapter.mapped;

import net.java.messageapi.converter.Converter;
import net.java.messageapi.converter.IdentityConverter;

public final class FieldMapping<BoundType> {

    public static FieldMapping<String> map(String attributeName) {
        return map(attributeName, null);
    }

    public static FieldMapping<String> mapWithDefault(String attributeName, String defaultValue) {
        return mapWithDefault(attributeName, null, defaultValue);
    }

    public static <BoundType> FieldMapping<BoundType> map(String attributeName,
            Converter<BoundType> converter) {
        return new FieldMapping<BoundType>(attributeName, converter);
    }

    public static <BoundType> FieldMapping<BoundType> mapWithDefault(String attributeName,
            Converter<BoundType> converter, BoundType defaultValue) {
        return new FieldMapping<BoundType>(attributeName, converter, defaultValue);
    }

    private final String attributeName;
    private final Converter<BoundType> converter;
    private final boolean hasDefaultValue;
    private final BoundType defaultValue;

    private FieldMapping(String attributeName, Converter<BoundType> converter) {
        this.attributeName = attributeName;
        this.converter = converterOrIdentity(converter);
        hasDefaultValue = false;
        defaultValue = null;
    }

    private FieldMapping(String attributeName, Converter<BoundType> converter,
            BoundType defaultValue) {
        this.attributeName = attributeName;
        this.converter = converterOrIdentity(converter);
        this.hasDefaultValue = true;
        this.defaultValue = defaultValue;
    }

    private Converter<BoundType> converterOrIdentity(
            @SuppressWarnings("hiding") Converter<BoundType> converter) {
        return (converter == null) ? new IdentityConverter<BoundType>() : converter;
    }

    public boolean hasDefaultValue() {
        return hasDefaultValue;
    }

    public BoundType getDefaultValue() {
        return defaultValue;
    }

    public String marshal(BoundType v) {
        try {
            return converter.marshal(v);
        } catch (Exception e) {
            throw new IllegalArgumentException("can't marshal " + v, e);
        }
    }

    public BoundType unmarshal(String v) {
        try {
            return converter.unmarshal(v);
        } catch (Exception e) {
            throw new IllegalArgumentException("can't unmarshal " + v, e);
        }
    }

    public String getAttributeName() {
        return attributeName;
    }

    public Converter<BoundType> getConverter() {
        return (converter instanceof IdentityConverter) ? null : converter;
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append("map ");
        out.append(attributeName);
        out.append(" with ");
        out.append(converter);
        if (hasDefaultValue) {
            out.append(" and default [");
            out.append(defaultValue);
            out.append("]");
        }
        return out.toString();
    }

}
