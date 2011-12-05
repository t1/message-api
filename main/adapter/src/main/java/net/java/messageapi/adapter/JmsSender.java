package net.java.messageapi.adapter;

import java.io.StringWriter;
import java.util.*;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.NamingException;

import net.java.messageapi.reflection.DelimiterWriter;

import org.slf4j.Logger;

import com.google.common.collect.Lists;

public class JmsSender {

    private final JmsQueueConfig config;
    private final JmsPayloadHandler payloadHandler;
    private final Logger logger;

    private Context jndiContext = null;
    private ConnectionFactory connectionFactory = null;
    private Destination destination = null;

    private final List<JmsHeaderSupplier> headers = Lists.<JmsHeaderSupplier> newArrayList(//
            new VersionSupplier(), new JmsPropertySupplier());

    public JmsSender(JmsQueueConfig config, JmsPayloadHandler payloadHandler, Logger logger) {
        if (config == null)
            throw new NullPointerException();
        this.config = config;
        if (payloadHandler == null)
            throw new NullPointerException();
        this.payloadHandler = payloadHandler;

        this.logger = logger;
    }

    protected void resetLookupCache() {
        jndiContext = null;
        connectionFactory = null;
        destination = null;
    }

    private Context getJNDIContext() {
        if (jndiContext == null) {
            try {
                jndiContext = config.getContext();
            } catch (NamingException e) {
                logger.error("can't get jndi context");
                throw new RuntimeException("can't get jndi context", e);
            }
        }
        return jndiContext;
    }

    protected ConnectionFactory getConnectionFactory() {
        if (connectionFactory == null) {
            String factoryName = config.getFactoryName();
            try {
                connectionFactory = (ConnectionFactory) getJNDIContext().lookup(factoryName);
            } catch (NamingException e) {
                String msg = "can't get connection factory " + factoryName;
                logger.error(msg);
                throw new RuntimeException(msg, e);
            }
        }
        return connectionFactory;
    }

    protected Destination getDestination() {
        if (destination == null) {
            String destinationName = config.getDestinationName();
            try {
                destination = (Destination) getJNDIContext().lookup(destinationName);
            } catch (NamingException e) {
                String msg = "can't get destination " + destinationName;
                logger.error(msg);
                throw new RuntimeException(msg, e);
            }
        }
        return destination;
    }

    public JmsQueueConfig getConfig() {
        return config;
    }

    public void sendJms(Object pojo) {
        Object payload = payloadHandler.toPayload(pojo);
        boolean transacted = config.isTransacted();

        // TODO back to debug level:
        logger.info("sending {}transacted message to {}", transacted ? "" : "non-", config.getDestinationName());
        logger.info("payload:\n{}", payload);

        Connection connection = null;
        boolean sent = false;

        try {
            ConnectionFactory factory = getConnectionFactory();
            connection = factory.createConnection(config.getUser(), config.getPass());
            Session session = connection.createSession(transacted, Session.AUTO_ACKNOWLEDGE);

            MessageProducer messageProducer = session.createProducer(getDestination());

            Message message = payloadHandler.createJmsMessage(payload, session);

            for (JmsHeaderSupplier header : headers) {
                header.addTo(message, pojo);
            }

            for (Map.Entry<String, Object> additionalProperty : config.getAdditionalProperties().entrySet()) {
                message.setObjectProperty(additionalProperty.getKey(), additionalProperty.getValue());
            }

            // TODO back to debug level:
            if (logger.isInfoEnabled())
                logger.info("properties: {}", properties(message));

            int deliveryMode = Message.DEFAULT_DELIVERY_MODE;
            int priority = Message.DEFAULT_PRIORITY;
            long timeToLive = Message.DEFAULT_TIME_TO_LIVE;

            messageProducer.send(message, deliveryMode, priority, timeToLive);

            logger.info("sent {} message id {} to {}",
                    new Object[] { payloadHandler.getName(), message.getJMSMessageID(), message.getJMSDestination() });
            sent = true;
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

    private String properties(Message message) throws JMSException {
        Enumeration<String> names = message.getPropertyNames();
        if (names == null)
            return "none";
        StringWriter result = new StringWriter();
        result.write("{ ");
        DelimiterWriter comma = new DelimiterWriter(result, ", ");
        while (names.hasMoreElements()) {
            comma.write();
            String name = names.nextElement();
            String value = message.getStringProperty(name);
            result.append(name).append("=").append(value);
        }
        result.append(" }");
        return result.toString();
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
        JmsSender other = (JmsSender) obj;
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