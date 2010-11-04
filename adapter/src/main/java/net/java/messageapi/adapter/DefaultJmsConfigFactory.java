package net.java.messageapi.adapter;

import java.util.Collections;
import java.util.Properties;

import net.java.messageapi.MessageApi;

import com.google.common.base.Suppliers;

/**
 * Factory to create {@link JmsConfig JMS endpoint configurations} for messages to be sent
 * transactionally to a local queue without making further assumptions on a messaging or application
 * server implementation. Objects created by this factory comply to the contract described in
 * {@link MessageApi}.
 */
public final class DefaultJmsConfigFactory {

    private DefaultJmsConfigFactory() {
        // just hide the constructor
    }

    public static JmsConfig getJmsConfig(String factoryName, String queueName, String user,
            String pass) {
        return new JmsConfig(factoryName, queueName, user, pass, true,
                Suppliers.ofInstance(new Properties()),
                Suppliers.ofInstance(Collections.<String, Object> emptyMap()));
    }
}
