package net.java.messageapi.adapter.xml;

import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.xml.bind.annotation.XmlRootElement;

import net.java.messageapi.MessageApi;
import net.java.messageapi.adapter.*;

/**
 * A {@link MessageSenderFactory} that sends the message to a local JMS queue, with the call
 * serialized as XML.
 * 
 * @see MessageApi
 */
@XmlRootElement
public class JmsXmlSenderFactory<T> extends AbstractJmsSenderFactory<T, String> {

    private final JaxbProvider jaxbProvider;

    // just to satisfy JAXB
    protected JmsXmlSenderFactory() {
        this.jaxbProvider = null;
    }

    public JmsXmlSenderFactory(Class<T> api, JmsConfig config) {
        this(api, config, JaxbProvider.UNCHANGED);
    }

    public JmsXmlSenderFactory(Class<T> api, JmsConfig config, JaxbProvider jaxbProvider) {
        super(api, config);
        this.jaxbProvider = jaxbProvider;
    }

    @Override
    protected String toPayload(Method method, Object[] args) {
        Writer writer = new StringWriter();
        T xmlSender = ToXmlEncoder.create(api, writer, jaxbProvider);

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
    protected javax.jms.Message createJmsMessage(String payload, Session session)
            throws JMSException {
        return session.createTextMessage(payload);
    }
}
