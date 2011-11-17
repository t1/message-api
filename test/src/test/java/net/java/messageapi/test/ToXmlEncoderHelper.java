package net.java.messageapi.test;

import java.io.Writer;
import java.lang.reflect.*;

import net.java.messageapi.adapter.*;

public class ToXmlEncoderHelper {
    public static <T> T create(Class<T> api, Writer writer) {
        return create(api, writer, JaxbProvider.UNCHANGED);
    }

    /**
     * @param jaxbProvider
     *            pass <code>null</code> to indicate that it should not be changed.
     */
    public static <T> T create(final Class<T> api, final Writer writer, final JaxbProvider jaxbProvider) {
        if (api == null)
            throw new NullPointerException("api must not be null");
        if (jaxbProvider == null)
            throw new NullPointerException("jaxbProvider must not be null; eventually pass JaxbProvider.UNCHANGED");
        if (writer == null)
            throw new NullPointerException("writer must not be null");

        InvocationHandler handler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) {
                Object pojo = new MessageCallFactory<Object>(method).apply(args);
                new XmlJmsPayloadHandler(jaxbProvider).convert(api, writer, pojo);
                return null;
            }
        };

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return api.cast(Proxy.newProxyInstance(classLoader, new Class<?>[] { api }, handler));
    }
}
