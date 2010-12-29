package net.java.messageapi.converter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.joda.time.LocalDate;
import org.joda.time.format.*;

@XmlRootElement
public class JodaLocalDateConverter extends Converter<LocalDate> {

    private String pattern;
    private transient DateTimeFormatter formatter;

    public JodaLocalDateConverter() {
        this(null);
    }

    public JodaLocalDateConverter(String pattern) {
        setPattern(pattern);
    }

    // required by JAXB
    @XmlAttribute(required = true)
    private void setPattern(String pattern) {
        this.pattern = pattern;
        this.formatter = (pattern == null) ? ISODateTimeFormat.date()
                : DateTimeFormat.forPattern(pattern);
    }

    // just to satisfy JAXB
    @SuppressWarnings("unused")
    private String getPattern() {
        return pattern;
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
