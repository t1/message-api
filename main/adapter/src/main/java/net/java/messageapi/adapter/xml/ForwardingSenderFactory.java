package net.java.messageapi.adapter.xml;

import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.*;

import net.java.messageapi.adapter.MessageSenderFactory;


/**
 * A {@link MessageSenderFactory} that produces a sender that -- when called -- serializes the call
 * to xml, deserializes it again and calls the receiver implementation of the same api. Quite handy
 * to test the complete serialization round trip.
 */
public class ForwardingSenderFactory<T> implements MessageSenderFactory<T> {

    public static <T> ForwardingSenderFactory<T> create(Class<T> api, T impl) {
        return create(api, impl, JaxbProvider.UNCHANGED);
    }

    public static <T> ForwardingSenderFactory<T> create(Class<T> api, T impl,
            JaxbProvider jaxbProvider) {
        return new ForwardingSenderFactory<T>(api, impl, jaxbProvider);
    }

    private final Class<T> api;
    private final XmlStringDecoder<T> decoder;
    private final JaxbProvider jaxbProvider;

    public ForwardingSenderFactory(Class<T> api, T impl, JaxbProvider jaxbProvider) {
        this.api = api;
        this.decoder = XmlStringDecoder.create(api, impl, jaxbProvider);
        this.jaxbProvider = jaxbProvider;
    }

    @Override
    public T get() {
        InvocationHandler handler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) {
                try {
                    forward(api, method, args);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    Throwable cause = e.getCause();
                    if (cause instanceof RuntimeException)
                        throw (RuntimeException) cause;
                    throw new RuntimeException(cause);
                }
                return null;
            }
        };

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return api.cast(Proxy.newProxyInstance(classLoader, new Class<?>[] { api }, handler));
    }

    private void forward(Class<T> api, Method method, Object[] args) throws IllegalAccessException,
            InvocationTargetException {
        Writer writer = new StringWriter();
        T xmlSender = ToXmlEncoder.create(api, writer, jaxbProvider);
        method.invoke(xmlSender, args);

        decoder.decode(writer.toString());
    }
}
