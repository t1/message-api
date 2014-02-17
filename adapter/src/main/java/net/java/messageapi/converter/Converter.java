package net.java.messageapi.converter;

import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Just rename {@link XmlAdapter} to <code>Converter</code> with the ValueType bound to String
 */
@XmlSeeAlso({ StringToBooleanConverter.class })
public abstract class Converter<BoundType> extends XmlAdapter<String, BoundType> {
    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
