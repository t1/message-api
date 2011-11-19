package net.java.messageapi.adapter;

import javax.jms.*;
import javax.xml.bind.annotation.XmlSeeAlso;

@XmlSeeAlso({ //
XmlJmsPayloadHandler.class, MapJmsPayloadHandler.class, SerializedJmsPayloadHandler.class })
public abstract class JmsPayloadHandler {

    public abstract Object toPayload(Object pojo);

    public abstract Message createJmsMessage(Object payload, Session session) throws JMSException;

    public abstract String getName();
}
