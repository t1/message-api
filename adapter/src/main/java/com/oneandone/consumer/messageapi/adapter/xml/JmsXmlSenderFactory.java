package com.oneandone.consumer.messageapi.adapter.xml;

import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.jms.JMSException;
import javax.jms.QueueSession;

import com.oneandone.consumer.messageapi.MessageApi;
import com.oneandone.consumer.messageapi.adapter.*;

/**
 * A {@link MessageSenderFactory} that sends the message to a local JMS queue, with the call
 * serialized as XML.
 * 
 * @see MessageApi
 */
public class JmsXmlSenderFactory<T> extends AbstractJmsSenderFactory<T, String> {

    public static <T> JmsXmlSenderFactory<T> create(Class<T> api, JmsConfig config) {
        return create(api, config, JaxbProvider.UNCHANGED);
    }

    public static <T> JmsXmlSenderFactory<T> create(Class<T> api, JmsConfig config,
            JaxbProvider jaxbProvider) {
        return new JmsXmlSenderFactory<T>(api, config, jaxbProvider);
    }

    private final JaxbProvider jaxbProvider;

    public JmsXmlSenderFactory(Class<T> api, JmsConfig config, JaxbProvider jaxbProvider) {
        super(api, config);
        this.jaxbProvider = jaxbProvider;
    }

    @Override
    protected String toPayload(Method method, Object[] args) {
        Writer writer = new StringWriter();
        T xmlSender = ToXmlEncoderFactory.create(api, jaxbProvider).get(writer);

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
    protected javax.jms.Message createJmsMessage(String payload, QueueSession session)
            throws JMSException {
        return session.createTextMessage(payload);
    }
}