package net.java.messageapi.adapter;

import java.lang.reflect.*;

import javax.xml.bind.annotation.*;

/**
 * A {@link MessageSenderFactory} that transfers calls using JMS. A {@link JmsQueueConfig} specifies
 * the JMS destination to use and a {@link JmsPayloadHandler} specifies the message type and handles
 * the message payload.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class JmsSenderFactory implements MessageSenderFactory {

    public static JmsSenderFactory create(JmsQueueConfig config, JmsPayloadHandler payloadHandler) {
        return new JmsSenderFactory(config, payloadHandler);
    }

    @XmlElement(name = "destination", required = true)
    private final JmsQueueConfig config;
    @XmlElementRef
    private final JmsPayloadHandler payloadHandler;

    // just to satisfy JAXB
    @SuppressWarnings("unused")
    private JmsSenderFactory() {
        this.config = null;
        this.payloadHandler = null;
    }

    public JmsSenderFactory(JmsQueueConfig config, JmsPayloadHandler payloadHandler) {
        if (config == null)
            throw new NullPointerException();
        this.config = config;
        if (payloadHandler == null)
            throw new NullPointerException();
        this.payloadHandler = payloadHandler;
    }

    public JmsQueueConfig getConfig() {
        return config;
    }

    public JmsPayloadHandler getPayloadHandler() {
        return payloadHandler;
    }

    @Override
    public <T> T create(final Class<T> api) {
        final JmsSender sender = new JmsSender(config, payloadHandler, api);
        InvocationHandler handler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) {
                sender.sendJms(method, args);
                return null;
            }
        };

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return api.cast(Proxy.newProxyInstance(classLoader, new Class<?>[] { api }, handler));
    }

    @Override
    public String toString() {
        return "JmsSenderFactory [config=" + config + ", payloadHandler=" + payloadHandler + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + config.hashCode();
        result = prime * result + payloadHandler.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        JmsSenderFactory other = (JmsSenderFactory) obj;
        if (!config.equals(other.config)) {
            return false;
        }
        if (!payloadHandler.equals(other.payloadHandler)) {
            return false;
        }
        return true;
    }
}
