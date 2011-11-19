package net.java.messageapi.adapter;

import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.*;

/**
 * A {@link MessageSenderFactory} that produces a sender that -- when called -- serializes the call to xml, deserializes
 * it again and calls the receiver implementation of the same api. Quite handy to test the complete serialization round
 * trip, but without JMS itself.
 */
public class ForwardingSenderFactory implements MessageSenderFactory {

    public static <T> T create(Class<T> api, T impl) {
        return create(api, impl, JaxbProvider.UNCHANGED);
    }

    @SuppressWarnings("unchecked")
    public static <T> T create(Class<T> api, T impl, JaxbProvider provider) {
        return new ForwardingSenderFactory((Class<Object>) api, impl, provider).create(api);
    }

    private final Class<Object> api;
    private final Object impl;
    private final JaxbProvider jaxbProvider;
    private final XmlJmsPayloadHandler payloadHandler;

    public ForwardingSenderFactory(Class<Object> api, Object impl) {
        this(api, impl, JaxbProvider.UNCHANGED);
    }

    public ForwardingSenderFactory(Class<Object> api, Object impl, JaxbProvider jaxbProvider) {
        assert api.isInstance(impl);
        this.api = api;
        this.impl = impl;
        this.jaxbProvider = jaxbProvider;
        this.payloadHandler = new XmlJmsPayloadHandler(jaxbProvider);
    }

    @Override
    public <T> T create(final Class<T> apiIn) {
        assert apiIn == api;
        InvocationHandler handler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) {
                forward(method, args);
                return null;
            }
        };

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return apiIn.cast(Proxy.newProxyInstance(classLoader, new Class<?>[] { api }, handler));
    }

    private <T> void forward(Method method, Object[] args) {
        Writer writer = new StringWriter();
        Object pojo = new MessageCallFactory<Object>(method).apply(args);
        payloadHandler.convert(writer, pojo);

        XmlStringDecoder<?> decoder = XmlStringDecoder.create(api, jaxbProvider);
        Object decoded = decoder.decode(writer.toString());
        PojoInvoker.<Object> of(api, impl).invoke(decoded);
    }
}
