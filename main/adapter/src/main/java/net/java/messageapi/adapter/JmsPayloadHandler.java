package net.java.messageapi.adapter;

import javax.jms.*;
import javax.xml.bind.annotation.XmlSeeAlso;

@XmlSeeAlso({ XmlJmsPayloadHandler.class, MapJmsPayloadHandler.class })
public abstract class JmsPayloadHandler {

    public abstract Object toPayload(Class<?> api, Object pojo);

    public abstract Message createJmsMessage(Object payload, Session session) throws JMSException;
}
