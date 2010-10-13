package com.oneandone.consumer.messageapi.adapter;

import java.lang.reflect.*;
import java.util.Map;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base implementation for a {@link MessageSenderFactory} that transfers the call using JMS.
 * Concrete implementations specify the message type and store the message payload.
 * 
 * @param <T>
 *            The type of the API, which is supplied by {@link #get()}
 * @param <M>
 *            The type of the payload that gets stored into the message.
 */
public abstract class AbstractJmsSenderFactory<T, M> implements MessageSenderFactory<T> {

    /**
     * The name of the property used to store the api version
     */
    private static final String VERSION = "VERSION";

    protected final Class<T> api;
    protected final JmsConfig config;
    private final Logger log;
    private Context jndiContext = null;
    private ConnectionFactory connectionFactory = null;
    private Destination destination = null;

    public AbstractJmsSenderFactory(Class<T> api, JmsConfig config) {
        if (api == null)
            throw new NullPointerException();
        this.api = api;
        if (config == null)
            throw new NullPointerException();
        this.config = config;
        this.log = LoggerFactory.getLogger(api);
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

    private Context getJNDIContext(JmsConfig config) throws NamingException {
        if (jndiContext == null) {
            jndiContext = config.getContext();
        }
        return jndiContext;
    }

    protected ConnectionFactory getConnectionFactory(JmsConfig config) throws NamingException {
        if (connectionFactory == null) {
            connectionFactory = (ConnectionFactory) getJNDIContext(config).lookup(
                    config.getFactoryName());
        }
        return connectionFactory;
    }

    protected Destination getDestination(JmsConfig config) throws NamingException {
        if (destination == null) {
            destination = (Destination) getJNDIContext(config).lookup(config.getDestinationName());
        }
        return destination;
    }

    public JmsConfig getConfig() {
        return config;
    }

    @Override
    public T get() {
        InvocationHandler handler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) {
                M payload = toPayload(method, args);
                sendJms(payload);
                return null;
            }
        };

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return api.cast(Proxy.newProxyInstance(classLoader, new Class<?>[] { api }, handler));
    }

    protected abstract M toPayload(Method method, Object[] args);

    protected void sendJms(M payload) {
        try {
            Connection connection = null;
            Session session = null;
            MessageProducer messageProducer = null;
            boolean sent = false;

            log.debug("sending to {} payload: {}", config.getFactoryName(), payload);

            try {
                ConnectionFactory factory = getConnectionFactory(config);
                connection = factory.createConnection(config.getUser(), config.getPass());
                session = connection.createSession(config.isTransacted(), Session.AUTO_ACKNOWLEDGE);

                Destination destination = getDestination(config);
                messageProducer = session.createProducer(destination);

                javax.jms.Message msg = createJmsMessage(payload, session);

                // TODO refactor this into a HeaderSupplier plugin mechanism
                String version = getApiVersion();
                if (version != null)
                    msg.setStringProperty(VERSION, version);

                for (Map.Entry<String, Object> additionalProperty : config.getAdditionalProperties().entrySet()) {
                    msg.setObjectProperty(additionalProperty.getKey(),
                            additionalProperty.getValue());
                }

                messageProducer.send(msg);

                log.info("sent message id {} to {}", msg.getJMSMessageID(), msg.getJMSDestination());
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

    protected abstract javax.jms.Message createJmsMessage(M body, Session session)
            throws JMSException;

    private String getApiVersion() {
        String version = api.getPackage().getSpecificationVersion();
        if (version == null)
            version = api.getPackage().getImplementationVersion();
        return version;
    }
}
