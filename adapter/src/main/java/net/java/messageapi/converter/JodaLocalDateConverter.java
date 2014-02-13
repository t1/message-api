package net.java.messageapi.converter;

import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.joda.time.LocalDate;
import org.joda.time.format.*;

@XmlRootElement
public class JodaLocalDateConverter extends Converter<LocalDate> {

    @XmlAttribute
    private final String pattern;
    private transient DateTimeFormatter formatter;

    public JodaLocalDateConverter() {
        this(null);
    }

    public JodaLocalDateConverter(String pattern) {
        this.pattern = pattern;
        initFormatter();
    }

    private void initFormatter() {
        this.formatter = (pattern == null) ? ISODateTimeFormat.date()
                : DateTimeFormat.forPattern(pattern);
    }

    // called by JAXB
    @SuppressWarnings("unused")
    private void afterMarshal(Marshaller marshaller) {
        initFormatter();
    }

    @Override
    public String marshal(LocalDate v) throws Exception {
        return v.toString(formatter);
    }

    @Override
    public LocalDate unmarshal(String v) throws Exception {
        return formatter.parseDateTime(v).toLocalDate();
    }

    @Override
    public String toString() {
        return super.toString() + "[" + ((pattern == null) ? "ISO" : pattern) + "]";
    }
}
