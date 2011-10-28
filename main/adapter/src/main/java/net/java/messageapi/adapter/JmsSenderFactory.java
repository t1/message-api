package net.java.messageapi.adapter;

import java.lang.reflect.*;
import java.util.List;
import java.util.Map;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.xml.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

/**
 * Base implementation for a {@link MessageSenderFactory} that transfers calls using JMS. A
 * {@link JmsQueueConfig} specifies the JMS destination to use and a {@link JmsPayloadHandler}
 * specifies the message type and handles the message payload.
 * 
 * @param <T>
 *            The type of the API, which is supplied by {@link #get()}
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

    @XmlTransient
    private Context jndiContext = null;
    @XmlTransient
    private ConnectionFactory connectionFactory = null;
    @XmlTransient
    private Destination destination = null;

    @XmlTransient
    private final List<JmsHeaderSupplier> headers = Lists.<JmsHeaderSupplier> newArrayList(
            new VersionSupplier(), new JmsPropertySupplier());

    // just to satisfy JAXB
    JmsSenderFactory() {
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

    protected void resetLookupCache() {
        jndiContext = null;
        connectionFactory = null;
        destination = null;
    }

    private Context getJNDIContext() throws NamingException {
        if (jndiContext == null) {
            jndiContext = config.getContext();
        }
        return jndiContext;
    }

    protected ConnectionFactory getConnectionFactory() throws NamingException {
        if (connectionFactory == null) {
            connectionFactory = (ConnectionFactory) getJNDIContext().lookup(config.getFactoryName());
        }
        return connectionFactory;
    }

    protected Destination getDestination() throws NamingException {
        if (destination == null) {
            destination = (Destination) getJNDIContext().lookup(config.getDestinationName());
        }
        return destination;
    }

    public JmsQueueConfig getConfig() {
        return config;
    }

    @Override
    public <T> T create(final Class<T> api) {
        InvocationHandler handler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) {
                sendJms(method, args);
                return null;
            }
        };

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return api.cast(Proxy.newProxyInstance(classLoader, new Class<?>[] { api }, handler));
    }

    protected void sendJms(Method method, Object[] args) {
        Connection connection = null;
        boolean sent = false;

        Class<?> api = method.getDeclaringClass();
        Object pojo = new MessageCallFactory<Object>(method).apply(args);
        Object payload = payloadHandler.toPayload(api, method, pojo);

        loggerFor(api).debug("sending to {} payload: {}", config.getDestinationName(), payload);

        try {
            ConnectionFactory factory = getConnectionFactory();
            connection = factory.createConnection(config.getUser(), config.getPass());
            Session session = connection.createSession(config.isTransacted(),
                    Session.AUTO_ACKNOWLEDGE);

            getDestination();
            MessageProducer messageProducer = session.createProducer(destination);

            Message message = payloadHandler.createJmsMessage(payload, session);

            for (JmsHeaderSupplier header : headers) {
                header.addTo(message, pojo);
            }

            for (Map.Entry<String, Object> additionalProperty : config.getAdditionalProperties().entrySet()) {
                message.setObjectProperty(additionalProperty.getKey(),
                        additionalProperty.getValue());
            }

            messageProducer.send(message);

            loggerFor(api).info("sent message id {} to {}", message.getJMSMessageID(),
                    message.getJMSDestination());
            sent = true;
        } catch (NamingException e) {
            throw new RuntimeException("can't send JMS", e);
        } catch (JMSException e) {
            throw new RuntimeException("can't send JMS", e);
        } finally {
            if (!sent) {
                resetLookupCache();
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                    // do nothing on errors during cleanup
                }
            }
        }
    }

    private Logger loggerFor(Class<?> api) {
        return LoggerFactory.getLogger(api);
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
        result = prime * result + headers.hashCode();
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
        if (!headers.equals(other.headers)) {
            return false;
        }
        if (!payloadHandler.equals(other.payloadHandler)) {
            return false;
        }
        return true;
    }

    public JmsPayloadHandler getPayloadHandler() {
        return payloadHandler;
    }
}
