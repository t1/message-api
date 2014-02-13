package net.java.messageapi.adapter;

import javax.jms.*;

import net.java.messageapi.MessageApi;

/**
 * Takes a {@link ObjectMessage}, deserializes it and calls the corresponding method in an implementation of the
 * matching {@link MessageApi}.
 * 
 * @param <T>
 *            the {@link MessageApi} interface that the calls are for and the <code>impl</code> implements.
 */
public class ObjectMessageDecoder<T> implements MessageListener {

    public static <T> ObjectMessageDecoder<T> of(Class<T> api, T impl) {
        return new ObjectMessageDecoder<T>(api, impl);
    }

    private final PojoInvoker<T> invoker;

    private ObjectMessageDecoder(Class<T> api, T impl) {
        this.invoker = new PojoInvoker<T>(api, impl);
    }

    @Override
    public void onMessage(final Message message) {
        Object pojo = getPayload(message);
        JmsPropertiesFromMessageToPojo.scan(message, pojo);
        invoker.invoke(pojo);
    }

    public Object getPayload(final Message message) {
        try {
            return ((ObjectMessage) message).getObject();
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }
}
