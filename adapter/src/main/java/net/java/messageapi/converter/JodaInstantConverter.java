package net.java.messageapi.converter;

import javax.xml.bind.annotation.XmlRootElement;

import org.joda.time.Instant;

@XmlRootElement
public class JodaInstantConverter extends Converter<Instant> {

    @Override
    public String marshal(Instant v) throws Exception {
        return v.toString();
    }

    @Override
    public Instant unmarshal(String v) throws Exception {
        return new Instant(v);
    }
}
