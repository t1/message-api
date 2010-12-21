package net.java.messageapi.adapter;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Enumeration;

import javax.xml.bind.*;

/**
 * The central provider for the proxies to send messages.
 * 
 * @see net.java.messageapi.MessageApi
 */
public class MessageSender {
    private static final String CONFIG_FILE_SUFFIX = ".config";
    private static final String DEFAULT_FILE_NAME = "default" + CONFIG_FILE_SUFFIX;

    public static <T> T of(Class<T> api) {
        return getConfigFor(api).create(api);
    }

    public static MessageSenderFactory getConfigFor(Class<?> api) {
        Reader reader = getReaderFor(api);
        return readConfigFrom(reader, api);
    }

    private static Reader getReaderFor(Class<?> api) {
        String fileName = api.getName() + CONFIG_FILE_SUFFIX;
        InputStream stream = getSingleUrlFor(fileName);
        if (stream == null)
            stream = getSingleUrlFor(DEFAULT_FILE_NAME);
        if (stream == null)
            throw new RuntimeException("found no config file [" + fileName + "]");
        return new InputStreamReader(stream, Charset.forName("utf-8"));
    }

    private static InputStream getSingleUrlFor(String fileName) {
        try {
            Enumeration<URL> resources = ClassLoader.getSystemResources(fileName);
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

    public static MessageSenderFactory readConfigFrom(Reader reader, Class<?> api) {
        try {
            JAXBContext context = JAXBContext.newInstance(JmsSenderFactory.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
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
