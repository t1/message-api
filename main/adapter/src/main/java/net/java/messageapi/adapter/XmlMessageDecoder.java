package net.java.messageapi.adapter;

import java.lang.reflect.Field;

import javax.jms.*;

import net.java.messageapi.JmsProperty;
import net.java.messageapi.MessageApi;

/**
 * Takes a {@link TextMessage}, deserializes it and calls the corresponding method in an
 * implementation of the matching {@link MessageApi}.
 * 
 * @param <T>
 *            the {@link MessageApi} interface that the calls are for and the <code>impl</code>
 *            implements.
 */
public class XmlMessageDecoder<T> implements MessageListener {

    public static <T> XmlMessageDecoder<T> create(Class<T> api, T impl) {
        return new XmlMessageDecoder<T>(api, impl);
    }

    public static <T> XmlMessageDecoder<T> create(Class<T> api, T impl, JaxbProvider jaxbProvider) {
        return new XmlMessageDecoder<T>(api, impl, jaxbProvider);
    }

    private final XmlStringDecoder<T> decoder;
    private final PojoInvoker<T> invoker;

    private XmlMessageDecoder(Class<T> api, T impl, XmlStringDecoder<T> decoder) {
        this.decoder = decoder;
        this.invoker = new PojoInvoker<T>(api, impl);
    }

    public XmlMessageDecoder(Class<T> api, T impl) {
        this(api, impl, XmlStringDecoder.create(api));
    }

    public XmlMessageDecoder(Class<T> api, T impl, JaxbProvider jaxbProvider) {
        this(api, impl, XmlStringDecoder.create(api, jaxbProvider));
    }

    @Override
    public void onMessage(final Message message) {
        String xml = getXml((TextMessage) message);
        Object pojo = decoder.decode(xml);
        new JmsPropertyScanner(new JmsPropertyScanner.Visitor() {
            @Override
            public void visit(String propertyName, Object container, Field field, Object index)
                    throws JMSException, IllegalAccessException {
                JmsProperty jmsProperty = field.getAnnotation(JmsProperty.class);
                if (jmsProperty != null && jmsProperty.headerOnly()) {
                    String value = message.getStringProperty(propertyName);
                    // FIXME handle other types and collections
                    field.set(container, value);
                }
            }
        }).scan(pojo);
        invoker.invoke(pojo);
    }

    private String getXml(TextMessage message) {
        try {
            return message.getText();
        } catch (JMSException e) {
            throw new RuntimeException("can't get text", e);
        }
    }
}
