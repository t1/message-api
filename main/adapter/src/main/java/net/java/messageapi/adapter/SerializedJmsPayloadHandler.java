package net.java.messageapi.adapter;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A {@link JmsPayloadHandler} that serializes calls as XML text messages.
 */
@XmlRootElement
public class SerializedJmsPayloadHandler extends JmsPayloadHandler {

    @Override
    public Object toPayload(Class<?> api, Object pojo) {
        return pojo;
    }

    @Override
    public javax.jms.Message createJmsMessage(Object payload, Session session) throws JMSException {
        return session.createObjectMessage((Serializable) payload);
    }

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

    @Override
    public String getName() {
        return "serialized";
    }
}
