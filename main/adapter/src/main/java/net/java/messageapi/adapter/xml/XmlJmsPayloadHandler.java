package net.java.messageapi.adapter.xml;

import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import net.java.messageapi.adapter.JmsPayloadHandler;

/**
 * A {@link JmsPayloadHandler} that serializes calls as XML text messages.
 */
@XmlRootElement
public class XmlJmsPayloadHandler extends JmsPayloadHandler {

    @XmlTransient
    private final JaxbProvider jaxbProvider;

    public XmlJmsPayloadHandler() {
        this(JaxbProvider.UNCHANGED);
    }

    public XmlJmsPayloadHandler(JaxbProvider jaxbProvider) {
        this.jaxbProvider = jaxbProvider;
    }

    @Override
    public String toPayload(Class<?> api, Method method, Object[] args) {
        Writer writer = new StringWriter();
        Object xmlSender = ToXmlEncoder.create(api, writer, jaxbProvider);

        try {
            method.invoke(xmlSender, args);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        return writer.toString();
    }

    @Override
    public javax.jms.Message createJmsMessage(Object payload, Session session) throws JMSException {
        return session.createTextMessage((String) payload);
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
}
