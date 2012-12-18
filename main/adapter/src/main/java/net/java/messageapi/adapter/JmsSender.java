package net.java.messageapi.adapter;

import java.io.StringWriter;
import java.util.*;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.NamingException;

import net.java.messageapi.reflection.DelimiterWriter;

import org.slf4j.Logger;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;

public class JmsSender {

    private final JmsQueueConfig config;
    private final JmsPayloadHandler payloadHandler;
    private final Logger logger;

    private Context jndiContext = null;
    private ConnectionFactory connectionFactory = null;

    private final List<JmsHeaderSupplier> headerSuppliers;
    private final Function<Object, String> destinationNameFunction;

    public JmsSender(JmsQueueConfig config, JmsPayloadHandler payloadHandler, Logger logger,
            Function<Object, String> destinationNameFunction) {
        if (config == null)
            throw new NullPointerException();
        this.config = config;

        if (payloadHandler == null)
            throw new NullPointerException();
        this.payloadHandler = payloadHandler;

        if (logger == null)
            throw new NullPointerException();
        this.logger = logger;

        this.destinationNameFunction = destinationNameFunction;

        this.headerSuppliers = loadHeaderSuppliers();
    }

    private List<JmsHeaderSupplier> loadHeaderSuppliers() {
        ImmutableList.Builder<JmsHeaderSupplier> builder = ImmutableList.builder();
        for (JmsHeaderSupplier service : ServiceLoader.load(JmsHeaderSupplier.class)) {
            logger.debug("add header supplier " + service.getClass().getName());
            builder.add(service);
        }
        return builder.build();
    }

    protected void resetLookupCache() {
        jndiContext = null;
        connectionFactory = null;
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

    private String getDestinationName(Object pojo) {
        if (destinationNameFunction != null) {
            String name = destinationNameFunction.apply(pojo);
            if (name != null) {
                return name;
            }
        }
        return config.getDestinationName();
    }

    private Destination getDestination(String destinationName) {
        try {
            return (Destination) getJNDIContext().lookup(destinationName);
        } catch (NamingException e) {
            String msg = "can't get destination " + destinationName;
            logger.error(msg);
            throw new RuntimeException(msg, e);
        }
    }

    public JmsQueueConfig getConfig() {
        return config;
    }

    public void sendJms(Object pojo) {
        Object payload = payloadHandler.toPayload(pojo);
        boolean transacted = config.isTransacted();
        String destinationName = getDestinationName(pojo);

        // TODO back to debug level:
        logger.info("sending {} transacted message to {}", transacted ? "manually" : "automatically", destinationName);
        logger.info("payload:\n{}", payload);

        Connection connection = null;
        boolean sent = false;

        try {
            ConnectionFactory factory = getConnectionFactory();
            connection = factory.createConnection(config.getUser(), config.getPass());
            Session session = connection.createSession(transacted, Session.AUTO_ACKNOWLEDGE);

            MessageProducer messageProducer = session.createProducer(getDestination(destinationName));

            Message message = payloadHandler.createJmsMessage(payload, session);

            for (JmsHeaderSupplier supplier : headerSuppliers) {
                logger.debug("adding header of " + supplier.getClass().getName());
                supplier.addTo(message, pojo);
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

            if (transacted)
                session.commit();

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
        @SuppressWarnings("unchecked")
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
        result = prime * result + headerSuppliers.hashCode();
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
        if (!headerSuppliers.equals(other.headerSuppliers)) {
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
