package com.oneandone.consumer.messageapi.adapter.xml;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.xml.bind.*;

import com.oneandone.consumer.messageapi.MessageApi;
import com.oneandone.consumer.messageapi.adapter.*;

/**
 * A {@link MessageSenderFactory} that sends the message to a local JMS queue, with the call
 * serialized as XML.
 * 
 * @see MessageApi
 */
public class JmsXmlSenderFactory<T> extends AbstractJmsSenderFactory<T, String> {

    public static <T> T createProxy(Class<T> api) {
        JmsConfig config = getConfigFor(api);
        return createProxy(api, config, JaxbProvider.UNCHANGED);
    }

    public static JmsConfig getConfigFor(Class<?> api) {
        try {
            JAXBContext context = JAXBContext.newInstance(JmsConfig.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            String fileName = api.getName() + "-jmsconfig.xml";
            System.out.println("load config from " + fileName);
            InputStream stream = api.getResourceAsStream(fileName);
            System.out.println("opened stream");
            return (JmsConfig) unmarshaller.unmarshal(stream);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T createProxy(Class<T> api, JmsConfig config) {
        return createProxy(api, config, JaxbProvider.UNCHANGED);
    }

    public static <T> T createProxy(Class<T> api, JmsConfig config, JaxbProvider jaxbProvider) {
        return createFactory(api, config, jaxbProvider).get();
    }

    public static <T> JmsXmlSenderFactory<T> createFactory(Class<T> api, JmsConfig config) {
        return createFactory(api, config, JaxbProvider.UNCHANGED);
    }

    public static <T> JmsXmlSenderFactory<T> createFactory(Class<T> api, JmsConfig config,
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
