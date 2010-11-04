package net.java.messageapi.converter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Just rename {@link XmlAdapter} to <code>Converter</code> with the ValueType bound to String
 */
public abstract class Converter<BoundType> extends XmlAdapter<String, BoundType> {
    // empty
}
