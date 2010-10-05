package com.oneandone.consumer.messageapi.converter;

import org.joda.time.Instant;


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
