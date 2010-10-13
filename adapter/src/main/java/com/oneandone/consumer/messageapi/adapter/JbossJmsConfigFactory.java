package com.oneandone.consumer.messageapi.adapter;

import java.util.*;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.oneandone.consumer.messageapi.MessageApi;

/**
 * Factory to create {@link JmsConfig JMS endpoint configurations} that can be used within a JBoss
 * environment. The factory name is set to @code{java:/JmsXA} for transactional sending and {@code
 * ConnectionFactory} otherwise. Note that non-transactional configurations do not comply to the
 * {@link MessageApi} contract, so use with caution.
 */
public final class JbossJmsConfigFactory {

    private static final Supplier<Map<String, Object>> DEFAULT_PROPERTIES = Suppliers.ofInstance(Collections.<String, Object> emptyMap());
    private static final Supplier<Properties> DEFAULT_CONTEXT = Suppliers.ofInstance(new Properties());

    private JbossJmsConfigFactory() {
        // just hide the constructor
    }

    private final static String FACTORY_TRANSACTED = "java:/JmsXA";
    private final static String FACTORY_NON_TRANSACTED = "ConnectionFactory";

    public static JmsConfig getTransactedJmsConfig(String queueName, String user, String pass) {
        return new JmsConfig(FACTORY_TRANSACTED, queueName, user, pass, true, DEFAULT_CONTEXT,
                DEFAULT_PROPERTIES);
    }

    public static JmsConfig getNonTransactedJmsConfig(String queueName, String user, String pass) {
        return new JmsConfig(FACTORY_NON_TRANSACTED, queueName, user, pass, false, DEFAULT_CONTEXT,
                DEFAULT_PROPERTIES);
    }

}
