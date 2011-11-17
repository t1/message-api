package net.java.messageapi.adapter;

import java.io.StringWriter;
import java.util.*;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.NamingException;

import net.java.messageapi.reflection.DelimiterWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class JmsSender {

    private final JmsQueueConfig config;
    private final JmsPayloadHandler payloadHandler;
    private final Class<?> api;
    private final Logger logger;

    private Context jndiContext = null;
    private ConnectionFactory connectionFactory = null;
    private Destination destination = null;

    private final List<JmsHeaderSupplier> headers = Lists.<JmsHeaderSupplier> newArrayList(
            new VersionSupplier(), new JmsPropertySupplier());

    public JmsSender(JmsQueueConfig config, JmsPayloadHandler payloadHandler, Class<?> api) {
        if (config == null)
            throw new NullPointerException();
        this.config = config;
        if (payloadHandler == null)
            throw new NullPointerException();
        this.payloadHandler = payloadHandler;
        if (api == null)
            throw new NullPointerException();
        this.api = api;

        this.logger = LoggerFactory.getLogger(api);
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

    public void sendJms(Object pojo) {
        Object payload = payloadHandler.toPayload(api, pojo);
        boolean transacted = config.isTransacted();

        // TODO back to debug level:
        logger.info("sending {}transacted message to {}", transacted ? "" : "non-",
                config.getDestinationName());
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
                message.setObjectProperty(additionalProperty.getKey(),
                        additionalProperty.getValue());
            }

            // TODO back to debug level:
            if (logger.isInfoEnabled())
                logger.info("properties: {}", properties(message));

            int deliveryMode = Message.DEFAULT_DELIVERY_MODE;
            int priority = Message.DEFAULT_PRIORITY;
            long timeToLive = Message.DEFAULT_TIME_TO_LIVE;

            messageProducer.send(message, deliveryMode, priority, timeToLive);

            logger.info("sent {} message id {} to {}", new Object[] { payloadHandler.getName(),
                    message.getJMSMessageID(), message.getJMSDestination() });
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
