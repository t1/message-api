package com.oneandone.consumer.messageapi.adapter.xml;

import javax.jms.*;

import com.oneandone.consumer.messageapi.MessageApi;

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

    public XmlMessageDecoder(XmlStringDecoder<T> decoder) {
        this.decoder = decoder;
    }

    public XmlMessageDecoder(Class<T> api, T impl) {
        this(XmlStringDecoder.create(api, impl));
    }

    public XmlMessageDecoder(Class<T> api, T impl, JaxbProvider jaxbProvider) {
        this(XmlStringDecoder.create(api, impl, jaxbProvider));
    }

    @Override
    public void onMessage(Message message) {
        String xml = getXml((TextMessage) message);
        decoder.decode(xml);
    }

    private String getXml(TextMessage message) {
        try {
            return message.getText();
        } catch (JMSException e) {
            throw new RuntimeException("can't get text", e);
        }
    }
}
