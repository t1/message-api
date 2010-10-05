package com.oneandone.consumer.messageapi.adapter.xml;

import java.io.Writer;
import java.lang.reflect.*;

import javax.xml.bind.*;

import com.oneandone.consumer.messageapi.adapter.MessageCallFactory;

/**
 * Creates instances for an interface that serialize the parameters to an XML written to some
 * {@link Writer}.
 */
public class ToXmlEncoderFactory<T> {

    public static <T> ToXmlEncoderFactory<T> create(Class<T> api) {
        return create(api, JaxbProvider.UNCHANGED);
    }

    /**
     * @param jaxbProvider
     *            pass <code>null</code> to indicate that it should not be changed.
     */
    public static <T> ToXmlEncoderFactory<T> create(Class<T> api, JaxbProvider jaxbProvider) {
        return new ToXmlEncoderFactory<T>(api, jaxbProvider);
    }

    private final Class<T> api;
    private final JaxbProvider jaxbProvider;

    private ToXmlEncoderFactory(Class<T> api, JaxbProvider jaxbProvider) {
        if (api == null)
            throw new NullPointerException("api must not be null");
        this.api = api;
        if (jaxbProvider == null)
            throw new NullPointerException(
                    "jaxbProvider must not be null; eventually pass JaxbProvider.UNCHANGED");
        this.jaxbProvider = jaxbProvider;
    }

    public T get(final Writer writer) {
        InvocationHandler handler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) {
                Marshaller marshaller = ToXmlEncoderFactory.this.createMarshaller();
                Object pojo = new MessageCallFactory<Object>(method).apply(args);
                ToXmlEncoderFactory.this.marshalPojo(marshaller, pojo, writer);
                return null;
            }
        };

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return api.cast(Proxy.newProxyInstance(classLoader, new Class<?>[] { api }, handler));
    }

    private Marshaller createMarshaller() {
        try {
            JAXBContext context = jaxbProvider.createJaxbContextFor(api.getPackage());
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            return marshaller;
        } catch (JAXBException e) {
            throw new RuntimeException("can't create marshaller for " + api, e);
        }
    }

    private void marshalPojo(Marshaller marshaller, Object pojo, Writer writer) {
        try {
            marshaller.marshal(pojo, writer);
        } catch (JAXBException e) {
            throw new RuntimeException("can't marshal " + pojo, e);
        }
    }
}
