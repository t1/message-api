package net.java.messageapi.adapter;

import javax.jms.*;

import net.java.messageapi.MessageApi;

/**
 * Automatically delegates to the proper {@link MapMessageDecoder} or {@link XmlMessageDecoder}
 */
public class MessageDecoder<T> implements MessageListener {

    private final Class<T> api;

    private XmlMessageDecoder<T> xmlMessageDecoder;
    private MapMessageDecoder<T> mapMessageDecoder;
    private ObjectMessageDecoder<T> objectMessageDecoder;

    public MessageDecoder() {
        this.api = findMessageApi(this.getClass());
    }

    private Class<T> findMessageApi(Class<?> type) {
        for (Class<?> interface_ : type.getInterfaces()) {
            if (interface_.isAnnotationPresent(MessageApi.class)) {
                @SuppressWarnings("unchecked")
                Class<T> result = (Class<T>) interface_;
                return result;
            }
        }
        Class<?> superclass = type.getSuperclass();
        if (superclass == MessageDecoder.class)
            throw new RuntimeException(this.getClass().getSimpleName() + " doesn't implement any message-apis");
        return findMessageApi(superclass);
    }

    /**
     * The {@link MessageDecoder} catches this method from the {@link MessageListener} and dispatches to one of the
     * {@link MessageApi} methods. Don't override this method, implement your business methods instead.
     */
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
            xmlMessageDecoder = XmlMessageDecoder.of(api, api.cast(this));
        return xmlMessageDecoder;
    }

    private MessageListener getMapMessageDecoder() {
        if (mapMessageDecoder == null)
            mapMessageDecoder = MapMessageDecoder.of(api, api.cast(this));
        return mapMessageDecoder;
    }

    private MessageListener getObjectMessageDecoder() {
        if (objectMessageDecoder == null)
            objectMessageDecoder = ObjectMessageDecoder.of(api, api.cast(this));
        return objectMessageDecoder;
    }
}
