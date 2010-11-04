@XmlJavaTypeAdapters( {
        @XmlJavaTypeAdapter(value = JodaInstantConverter.class, type = Instant.class),
        @XmlJavaTypeAdapter(value = JodaLocalDateConverter.class, type = LocalDate.class) })
@XmlSchema(namespace = "http://messageapi.java.net")
package net.java.messageapi.test.defaultjaxb;

import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;

import net.java.messageapi.converter.JodaInstantConverter;
import net.java.messageapi.converter.JodaLocalDateConverter;

import org.joda.time.Instant;
import org.joda.time.LocalDate;


