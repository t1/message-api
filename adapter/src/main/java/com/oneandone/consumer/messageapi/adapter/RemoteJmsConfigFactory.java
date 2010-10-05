package com.oneandone.consumer.messageapi.adapter;

import java.util.Collections;
import java.util.Properties;

import com.google.common.base.Suppliers;

public final class RemoteJmsConfigFactory {

    private static final String FACTORY = "ConnectionFactory";

    private RemoteJmsConfigFactory() {

    }

    public static JmsConfig getRemoteJmsConfig(String providerUrl, String queueName,
            String queueUser, String queuePass) {
        return new JmsConfig(FACTORY, queueName, queueUser, queuePass, false,
                Suppliers.ofInstance(createContextProperties(providerUrl)),
                Suppliers.ofInstance(Collections.<String, Object> emptyMap()));
    }

    private static Properties createContextProperties(String providerUrl) {
        Properties properties = new Properties();
        properties.put("java.naming.factory.initial", "org.jnp.interfaces.NamingContextFactory");
        properties.put("java.naming.factory.url.pkgs", "org.jboss.naming:org.jnp.interface");
        properties.put("java.naming.provider.url", providerUrl);
        return properties;
    }
}
