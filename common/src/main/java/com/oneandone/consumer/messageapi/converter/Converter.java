package com.oneandone.consumer.messageapi.converter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public abstract class Converter<BoundType> extends XmlAdapter<String, BoundType> {

}
