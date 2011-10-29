package net.java.messageapi.adapter;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Enumeration;

import javax.xml.bind.*;


/**
 * The central provider for the proxies to send messages.
 * <p>
 * If you need your own converters or stuff in the {@link JAXBContext}, you can
 * {@link #setContext(JAXBContext) set} it.
 * 
 * @see net.java.messageapi.MessageApi
 */
public class MessageSender {
    private static final String CONFIG_FILE_SUFFIX = ".config";
    private static final String DEFAULT_FILE_NAME = "default" + CONFIG_FILE_SUFFIX;

    private static JAXBContext context;

    public static JAXBContext getContext() {
        if (context == null) {
            try {
                context = JAXBContext.newInstance(JmsSenderFactory.class);
            } catch (JAXBException e) {
                throw new RuntimeException(e);
            }
        }
        return MessageSender.context;
    }

    public static JAXBContext setContext(JAXBContext context) {
        JAXBContext old = MessageSender.context;
        MessageSender.context = context;
        return old;
    }

    public static <T> T of(Class<T> api) {
        return getConfigFor(api).create(api);
    }

    /** Assumes that the config for that api is a mapped jms config */
    public static Mapping getJmsMappingFor(Class<?> api) {
        JmsSenderFactory config = (JmsSenderFactory) getConfigFor(api);
        MapJmsPayloadHandler payloadHandler = (MapJmsPayloadHandler) config.getPayloadHandler();
        return payloadHandler.mapping;
    }

    public static MessageSenderFactory getConfigFor(Class<?> api) {
        Reader reader = getReaderFor(api);
        if (reader == null)
            return newDefaultConfigFor(api);
        return readConfigFrom(reader);
    }

    static Reader getReaderFor(Class<?> api) {
        ClassLoader classLoader = api.getClassLoader();
        String fileName = api.getName() + CONFIG_FILE_SUFFIX;
        InputStream stream = getSingleUrlFor(classLoader, fileName);
        if (stream == null)
            stream = getSingleUrlFor(classLoader, DEFAULT_FILE_NAME);
        if (stream == null)
            return null;
        return new InputStreamReader(stream, Charset.forName("utf-8"));
    }

    static MessageSenderFactory newDefaultConfigFor(Class<?> api) {
        JmsQueueConfig config = new JmsQueueConfig("ConnectionFactory", api.getName(), null, null,
                true, null, null);
        return new JmsSenderFactory(config, new XmlJmsPayloadHandler());
    }

    private static InputStream getSingleUrlFor(ClassLoader classLoader, String fileName) {
        try {
            Enumeration<URL> resources = classLoader.getResources(fileName);
            if (!resources.hasMoreElements())
                return null;
            URL result = resources.nextElement();
            if (resources.hasMoreElements())
                throw new RuntimeException("found multiple configs files [" + fileName + "]");
            return result.openStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static MessageSenderFactory readConfigFrom(Reader reader) {
        try {
            Unmarshaller unmarshaller = getContext().createUnmarshaller();
            MessageSenderFactory factory = (MessageSenderFactory) unmarshaller.unmarshal(reader);
            return factory;
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    private MessageSender() {
        // this is just a singleton
    }
}
