package net.java.messageapi.converter;

import javax.xml.bind.annotation.XmlRootElement;

import org.joda.time.LocalDate;
import org.joda.time.format.*;

@XmlRootElement
public class JodaLocalDateConverter extends Converter<LocalDate> {

    private final DateTimeFormatter formatter;

    public JodaLocalDateConverter() {
        this.formatter = ISODateTimeFormat.date();
    }

    public JodaLocalDateConverter(String pattern) {
        this.formatter = DateTimeFormat.forPattern(pattern);
    }

    @Override
    public String marshal(LocalDate v) throws Exception {
        return v.toString(formatter);
    }

    @Override
    public LocalDate unmarshal(String v) throws Exception {
        return formatter.parseDateTime(v).toLocalDate();
    }

}
