package net.java.messageapi.adapter;

import java.lang.reflect.Method;

import javax.jms.*;
import javax.xml.bind.annotation.XmlSeeAlso;

import net.java.messageapi.adapter.mapped.MapJmsPayloadHandler;
import net.java.messageapi.adapter.xml.XmlJmsPayloadHandler;

@XmlSeeAlso({ XmlJmsPayloadHandler.class, MapJmsPayloadHandler.class })
public abstract class JmsPayloadHandler {

    public abstract Object toPayload(Class<?> api, Method method, Object[] args);

    public abstract Message createJmsMessage(Object payload, Session session) throws JMSException;

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        return true;
    }
}
