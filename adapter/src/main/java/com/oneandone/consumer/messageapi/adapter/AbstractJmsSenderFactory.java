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
    private QueueConnectionFactory queueConnectionFactory = null;
    private Queue queue = null;

    public AbstractJmsSenderFactory(Class<T> api, JmsConfig config) {
        if (api == null)
            throw new NullPointerException();
        this.api = api;
        if (config == null)
            throw new NullPointerException();
        this.config = config;
        this.log = LoggerFactory.getLogger(api);
    }

    protected void close(QueueSender queueSender) throws JMSException {
        if (queueSender != null) {
            try {
                queueSender.close();
            } catch (Exception e) {
                // do nothing on errors during cleanup
            }
        }
    }

    protected void close(QueueSession session) throws JMSException {
        if (session != null) {
            try {
                session.close();
            } catch (Exception e) {
                // do nothing on errors during cleanup
            }
        }
    }

    protected void close(QueueConnection connection) throws JMSException {
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
        queueConnectionFactory = null;
        queue = null;
    }

    private Context getJNDIContext(JmsConfig config) throws NamingException {
        if (jndiContext == null) {
            jndiContext = config.getContext();
        }
        return jndiContext;
    }

    protected QueueConnectionFactory getQueueConnectionFactory(JmsConfig config)
            throws NamingException {
        if (queueConnectionFactory == null) {
            queueConnectionFactory = (QueueConnectionFactory) getJNDIContext(config).lookup(
                    config.getFactoryName());
        }
        return queueConnectionFactory;
    }

    protected Queue getQueue(JmsConfig config) throws NamingException {
        if (queue == null) {
            queue = (Queue) getJNDIContext(config).lookup(config.getQueueName());
        }
        return queue;
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
            QueueConnection connection = null;
            QueueSession session = null;
            QueueSender queueSender = null;
            boolean sent = false;

            log.debug("sending to {} payload: {}", config.getFactoryName(), payload);

            try {
                QueueConnectionFactory factory = getQueueConnectionFactory(config);
                connection = factory.createQueueConnection(config.getUser(), config.getPass());
                session = connection.createQueueSession(config.isTransacted(),
                        Session.AUTO_ACKNOWLEDGE);

                Queue queue = getQueue(config);
                queueSender = session.createSender(queue);

                javax.jms.Message msg = createJmsMessage(payload, session);

                // TODO refactor this into a HeaderSupplier plugin mechanism
                String version = getApiVersion();
                if (version != null)
                    msg.setStringProperty(VERSION, version);

                for (Map.Entry<String, Object> additionalProperty : config.getAdditionalProperties().entrySet()) {
                    msg.setObjectProperty(additionalProperty.getKey(),
                            additionalProperty.getValue());
                }

                queueSender.send(msg);

                log.info("sent message id {} to {}", msg.getJMSMessageID(), msg.getJMSDestination());
                sent = true;
            } finally {
                if (!sent) {
                    resetLookupCache();
                }
                close(queueSender);
                close(session);
                close(connection);
            }
        } catch (NamingException e) {
            throw new RuntimeException("can't send JMS", e);
        } catch (JMSException e) {
            throw new RuntimeException("can't send JMS", e);
        }
    }

    protected abstract javax.jms.Message createJmsMessage(M body, QueueSession session)
            throws JMSException;

    private String getApiVersion() {
        String version = api.getPackage().getSpecificationVersion();
        if (version == null)
            version = api.getPackage().getImplementationVersion();
        return version;
    }
}
