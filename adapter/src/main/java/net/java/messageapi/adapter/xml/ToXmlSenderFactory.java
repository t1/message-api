package net.java.messageapi.adapter.xml;

import java.io.Writer;

import net.java.messageapi.adapter.MessageSenderFactory;


/**
 * Simple {@link MessageSenderFactory} that serializes the parameters to an XML written to some
 * {@link Writer}; very handy for testing.
 */
public class ToXmlSenderFactory<T> implements MessageSenderFactory<T> {

    public static <T> ToXmlSenderFactory<T> create(Class<T> api, Writer writer) {
        return create(api, JaxbProvider.UNCHANGED, writer);
    }

    public static <T> ToXmlSenderFactory<T> create(Class<T> api, JaxbProvider jaxbProvider,
            Writer writer) {
        return new ToXmlSenderFactory<T>(api, jaxbProvider, writer);
    }

    private final Class<T> api;
    private final JaxbProvider jaxbProvider;
    private final Writer writer;

    public ToXmlSenderFactory(Class<T> api, JaxbProvider jaxbProvider, Writer writer) {
        this.api = api;
        this.jaxbProvider = jaxbProvider;
        this.writer = writer;
    }

    @Override
    public T get() {
        return ToXmlEncoder.create(api, writer, jaxbProvider);
    }
}
