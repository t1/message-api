package net.java.messageapi.adapter;

import java.io.*;
import java.lang.reflect.Method;
import java.util.*;

import javax.xml.bind.*;

import net.java.messageapi.MessageApi;

/**
 * Takes XML Strings, deserializes them and calls the corresponding methods in an implementation of some
 * {@link MessageApi}.
 * 
 * @param <T>
 *            the {@link MessageApi} interface that the calls are for and the <code>impl</code> implements.
 */
public class XmlStringDecoder<T> {

    public static <T> XmlStringDecoder<T> create(Class<T> api) {
        return create(api, JaxbProvider.UNCHANGED);
    }

    public static <T> XmlStringDecoder<T> create(Class<T> api, JaxbProvider jaxbProvider) {
        return new XmlStringDecoder<T>(api, jaxbProvider);
    }

    private final Unmarshaller unmarshaller;

    private XmlStringDecoder(Class<T> api, JaxbProvider jaxbProvider) {
        if (jaxbProvider == null)
            throw new NullPointerException("jaxbProvider must not be null; eventually pass JaxbProvider.UNCHANGED");
        this.unmarshaller = createUnmarshaller(api, jaxbProvider);
        // TODO verify by calling unmarshaller.setSchema(...)
    }

    private Unmarshaller createUnmarshaller(Class<T> api, JaxbProvider jaxbProvider) {
        JAXBContext context = getContext(api, jaxbProvider);
        try {
            return context.createUnmarshaller();
        } catch (JAXBException e) {
            throw new RuntimeException("can't unmarshal", e);
        }
    }

    protected JAXBContext getContext(Class<T> api, JaxbProvider jaxbProvider) {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        for (Method method : api.getMethods()) {
            Class<?> type = new MethodAsClassGenerator(method).get();
            classes.add(type);
        }
        Class<?>[] classesToBeBound = classes.toArray(new Class[classes.size()]);
        return jaxbProvider.createJaxbContextFor(classesToBeBound);
    }

    public Object decode(String xml) {
        Reader reader = new StringReader(xml);
        return readPojo(reader);
    }

    private Object readPojo(Reader reader) {
        try {
            return unmarshaller.unmarshal(reader);
        } catch (JAXBException e) {
            throw new RuntimeException("can't unmarshal", e);
        }
    }
}
