@XmlJavaTypeAdapters( {
        @XmlJavaTypeAdapter(value = JodaInstantConverter.class, type = Instant.class),
        @XmlJavaTypeAdapter(value = JodaLocalDateConverter.class, type = LocalDate.class) })
@XmlSchema(namespace = "http://www.oneandone.com/consumer/tools/messaging")
package com.oneandone.consumer.messageapi.test.defaultjaxb;

import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;

import org.joda.time.Instant;
import org.joda.time.LocalDate;

import com.oneandone.consumer.messageapi.converter.JodaInstantConverter;
import com.oneandone.consumer.messageapi.converter.JodaLocalDateConverter;

