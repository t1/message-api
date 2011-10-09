package net.java.messageapi.adapter.xml;

import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.*;

import net.java.messageapi.adapter.MessageCallFactory;
import net.java.messageapi.adapter.MessageSenderFactory;

/**
 * A {@link MessageSenderFactory} that produces a sender that -- when called -- serializes the call
 * to xml, deserializes it again and calls the receiver implementation of the same api. Quite handy
 * to test the complete serialization round trip.
 */
public class ForwardingSenderFactory implements MessageSenderFactory {

    private final Object impl;
    private final JaxbProvider jaxbProvider;
    private final XmlJmsPayloadHandler payloadHandler = new XmlJmsPayloadHandler();

    public ForwardingSenderFactory(Object impl) {
        this(impl, JaxbProvider.UNCHANGED);
    }

    public ForwardingSenderFactory(Object impl, JaxbProvider jaxbProvider) {
        this.impl = impl;
        this.jaxbProvider = jaxbProvider;
    }

    @Override
    public <T> T create(final Class<T> api) {
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

    private <T> void forward(Class<T> api, Method method, Object[] args)
            throws IllegalAccessException, InvocationTargetException {
        Writer writer = new StringWriter();
        Object pojo = new MessageCallFactory<Object>(method).apply(args);
        payloadHandler.convert(api, writer, jaxbProvider, pojo);

        XmlStringDecoder<T> decoder = XmlStringDecoder.create(api, api.cast(impl), jaxbProvider);
        decoder.decode(writer.toString());
    }
}
