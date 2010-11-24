package net.java.messageapi.adapter;

import java.util.*;

import net.java.messageapi.MessageApi;

/**
 * Factory to create {@link JmsConfig JMS endpoint configurations} that can be used within a JBoss
 * environment. The factory name is set to @code{java:/JmsXA} for transactional sending and {@code
 * ConnectionFactory} otherwise. Note that non-transactional configurations do not comply to the
 * {@link MessageApi} contract, so use with caution.
 */
public final class JbossJmsConfigFactory {

    private static final Map<String, Object> DEFAULT_PROPERTIES = Collections.<String, Object> emptyMap();
    private static final Properties DEFAULT_CONTEXT = new Properties();

    private JbossJmsConfigFactory() {
        // just hide the constructor
    }

    private final static String FACTORY_TRANSACTED = "java:/JmsXA";
    private final static String FACTORY_NON_TRANSACTED = "ConnectionFactory";

    public static JmsConfig getTransactedJmsConfig(String queueName, String user, String pass,
            JmsSenderFactoryType type) {
        return JmsConfig.getJmsConfig(FACTORY_TRANSACTED, queueName, user, pass, true,
                DEFAULT_CONTEXT, DEFAULT_PROPERTIES, type);
    }

    public static JmsConfig getNonTransactedJmsConfig(String queueName, String user, String pass,
            JmsSenderFactoryType type) {
        return JmsConfig.getJmsConfig(FACTORY_NON_TRANSACTED, queueName, user, pass, false,
                DEFAULT_CONTEXT, DEFAULT_PROPERTIES, type);
    }
}
