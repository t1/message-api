package net.java.messageapi.converter;

import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Just rename {@link XmlAdapter} to <code>Converter</code> with the ValueType bound to String
 * 
 * FIXME this introduces a dependency to JodaTime, which should be optional!
 */
@XmlSeeAlso({ JodaInstantConverter.class, JodaLocalDateConverter.class,
        StringToBooleanConverter.class })
public abstract class Converter<BoundType> extends XmlAdapter<String, BoundType> {
    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
