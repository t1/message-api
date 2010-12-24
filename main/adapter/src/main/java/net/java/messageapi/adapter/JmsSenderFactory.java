package net.java.messageapi.adapter;

import java.lang.reflect.*;
import java.util.Map;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.xml.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    /**
     * The name of the property used to store the api version
     */
    private static final String VERSION = "VERSION";

    @XmlElement(name = "destination", required = true)
    private final JmsQueueConfig config;
    @XmlElementRef
    JmsPayloadHandler payloadHandler;

    @XmlTransient
    private Context jndiContext = null;
    @XmlTransient
    private ConnectionFactory connectionFactory = null;
    @XmlTransient
    private Destination destination = null;

    // just to satisfy JAXB
    protected JmsSenderFactory() {
        this.config = null;
    }

    public JmsSenderFactory(JmsQueueConfig config, JmsPayloadHandler payloadHandler) {
        if (config == null)
            throw new NullPointerException();
        this.config = config;
        if (payloadHandler == null)
            throw new NullPointerException();
        this.payloadHandler = payloadHandler;
    }

    protected void close(MessageProducer messageProducer) {
        if (messageProducer != null) {
            try {
                messageProducer.close();
            } catch (Exception e) {
                // do nothing on errors during cleanup
            }
        }
    }

    protected void close(Session session) {
        if (session != null) {
            try {
                session.close();
            } catch (Exception e) {
                // do nothing on errors during cleanup
            }
        }
    }

    protected void close(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (Exception e) {
                // do nothing on errors during cleanup
            }
        }
    }

    protected void resetLookupCache() {
        jndiContext = null;
        connectionFactory = null;
        destination = null;
    }

    private Context getJNDIContext(JmsQueueConfig config) throws NamingException {
        if (jndiContext == null) {
            jndiContext = config.getContext();
        }
        return jndiContext;
    }

    protected ConnectionFactory getConnectionFactory(JmsQueueConfig config) throws NamingException {
        if (connectionFactory == null) {
            connectionFactory = (ConnectionFactory) getJNDIContext(config).lookup(
                    config.getFactoryName());
        }
        return connectionFactory;
    }

    protected Destination getDestination(JmsQueueConfig config) throws NamingException {
        if (destination == null) {
            destination = (Destination) getJNDIContext(config).lookup(config.getDestinationName());
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
                Object payload = payloadHandler.toPayload(api, method, args);
                sendJms(api, payload);
                return null;
            }
        };

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return api.cast(Proxy.newProxyInstance(classLoader, new Class<?>[] { api }, handler));
    }

    protected void sendJms(Class<?> api, Object payload) {
        try {
            Connection connection = null;
            Session session = null;
            MessageProducer messageProducer = null;
            boolean sent = false;

            log(api).debug("sending to {} payload: {}", config.getFactoryName(), payload);

            try {
                ConnectionFactory factory = getConnectionFactory(config);
                connection = factory.createConnection(config.getUser(), config.getPass());
                session = connection.createSession(config.isTransacted(), Session.AUTO_ACKNOWLEDGE);

                Destination destination = getDestination(config);
                messageProducer = session.createProducer(destination);

                javax.jms.Message msg = payloadHandler.createJmsMessage(payload, session);

                // TODO refactor this into a HeaderSupplier plugin mechanism
                String version = getApiVersion(api);
                if (version != null)
                    msg.setStringProperty(VERSION, version);

                for (Map.Entry<String, Object> additionalProperty : config.getAdditionalProperties().entrySet()) {
                    msg.setObjectProperty(additionalProperty.getKey(),
                            additionalProperty.getValue());
                }

                messageProducer.send(msg);

                log(api).info("sent message id {} to {}", msg.getJMSMessageID(),
                        msg.getJMSDestination());
                sent = true;
            } finally {
                if (!sent) {
                    resetLookupCache();
                }
                close(messageProducer);
                close(session);
                close(connection);
            }
        } catch (NamingException e) {
            throw new RuntimeException("can't send JMS", e);
        } catch (JMSException e) {
            throw new RuntimeException("can't send JMS", e);
        }
    }

    private Logger log(Class<?> api) {
        return LoggerFactory.getLogger(api);
    }

    private String getApiVersion(Class<?> api) {
        String version = api.getPackage().getSpecificationVersion();
        if (version == null)
            version = api.getPackage().getImplementationVersion();
        return version;
    }

    @Override
    public String toString() {
        return "JmsSenderFactory [config=" + config + ", payloadHandler=" + payloadHandler + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((config == null) ? 0 : config.hashCode());
        result = prime * result + ((payloadHandler == null) ? 0 : payloadHandler.hashCode());
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
        if (config == null) {
            if (other.config != null) {
                return false;
            }
        } else if (!config.equals(other.config)) {
            return false;
        }
        if (payloadHandler == null) {
            if (other.payloadHandler != null) {
                return false;
            }
        } else if (!payloadHandler.equals(other.payloadHandler)) {
            return false;
        }
        return true;
    }

}
