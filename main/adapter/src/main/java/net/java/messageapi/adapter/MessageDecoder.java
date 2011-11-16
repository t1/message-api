package net.java.messageapi.adapter;

import javax.jms.*;

/**
 * Automatically delegates to the proper {@link MapMessageDecoder} or {@link XmlMessageDecoder}
 */
public class MessageDecoder<T> implements MessageListener {

    public static <T> MessageDecoder<T> of(Class<T> api, T impl) {
        return new MessageDecoder<T>(api, impl);
    }

    private final Class<T> api;
    private final T impl;

    private XmlMessageDecoder<T> xmlMessageDecoder;
    private MapMessageDecoder<T> mapMessageDecoder;
    private ObjectMessageDecoder<T> objectMessageDecoder;

    public MessageDecoder(Class<T> api, T impl) {
        this.api = api;
        this.impl = impl;
    }

    @Override
    public void onMessage(Message message) {
        if (message instanceof TextMessage) {
            getXmlMessageDecoder().onMessage(message);
        } else if (message instanceof MapMessage) {
            getMapMessageDecoder().onMessage(message);
        } else if (message instanceof ObjectMessage) {
            getObjectMessageDecoder().onMessage(message);
        } else {
            throw new RuntimeException("can't handle " + message.getClass().getSimpleName() + "s");
        }
    }

    private MessageListener getXmlMessageDecoder() {
        if (xmlMessageDecoder == null)
            xmlMessageDecoder = XmlMessageDecoder.of(api, impl);
        return xmlMessageDecoder;
    }

    private MessageListener getMapMessageDecoder() {
        if (mapMessageDecoder == null)
            mapMessageDecoder = MapMessageDecoder.of(api, impl);
        return mapMessageDecoder;
    }

    private MessageListener getObjectMessageDecoder() {
        if (objectMessageDecoder == null)
            objectMessageDecoder = ObjectMessageDecoder.of(api, impl);
        return objectMessageDecoder;
    }
}
