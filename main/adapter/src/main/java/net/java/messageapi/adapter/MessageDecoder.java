package net.java.messageapi.adapter;

import javax.jms.*;

import net.java.messageapi.adapter.mapped.MapMessageDecoder;
import net.java.messageapi.adapter.xml.XmlMessageDecoder;

/**
 * Automatically delegates to the proper {@link MapMessageDecoder} or {@link XmlMessageDecoder}
 */
public class MessageDecoder<T> implements MessageListener {

    private final Class<T> api;
    private final T impl;

    private XmlMessageDecoder<T> xmlMessageDecoder;
    private MapMessageDecoder<T> mapMessageDecoder;

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
        } else {
            throw new RuntimeException("can't handle " + message.getClass().getSimpleName() + "s");
        }
    }

    private MessageListener getXmlMessageDecoder() {
        if (xmlMessageDecoder == null)
            xmlMessageDecoder = new XmlMessageDecoder<T>(api, impl);
        return xmlMessageDecoder;
    }

    private MessageListener getMapMessageDecoder() {
        if (mapMessageDecoder == null)
            mapMessageDecoder = new MapMessageDecoder<T>(api, impl);
        return mapMessageDecoder;
    }
}
