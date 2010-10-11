package com.oneandone.consumer.messageapi.adapter.xml;

import java.io.Writer;
import java.lang.reflect.*;

import javax.xml.bind.*;

import com.oneandone.consumer.messageapi.adapter.MessageCallFactory;

/**
 * Creates instances for an interface that serialize the parameters to an XML written to some
 * {@link Writer}.
 */
public class ToXmlEncoder<T> {

    public static <T> T create(Class<T> api, Writer writer) {
        return create(api, writer, JaxbProvider.UNCHANGED);
    }

    /**
     * @param jaxbProvider
     *            pass <code>null</code> to indicate that it should not be changed.
     */
    public static <T> T create(final Class<T> api, final Writer writer,
            final JaxbProvider jaxbProvider) {
        if (api == null)
            throw new NullPointerException("api must not be null");
        if (jaxbProvider == null)
            throw new NullPointerException(
                    "jaxbProvider must not be null; eventually pass JaxbProvider.UNCHANGED");
        if (writer == null)
            throw new NullPointerException("writer must not be null");

        InvocationHandler handler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) {
                Marshaller marshaller = createMarshaller(api, jaxbProvider);
                Object pojo = new MessageCallFactory<Object>(method).apply(args);
                marshalPojo(marshaller, pojo, writer);
                return null;
            }
        };

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return api.cast(Proxy.newProxyInstance(classLoader, new Class<?>[] { api }, handler));
    }

    private static <T> Marshaller createMarshaller(Class<T> api, JaxbProvider jaxbProvider) {
        try {
            JAXBContext context = jaxbProvider.createJaxbContextFor(api.getPackage());
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            return marshaller;
        } catch (JAXBException e) {
            throw new RuntimeException("can't create marshaller for " + api, e);
        }
    }

    private static void marshalPojo(Marshaller marshaller, Object pojo, Writer writer) {
        try {
            marshaller.marshal(pojo, writer);
        } catch (JAXBException e) {
            throw new RuntimeException("can't marshal " + pojo, e);
        }
    }
}
