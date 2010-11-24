package net.java.messageapi.adapter;

import java.util.Collections;
import java.util.Properties;

public final class RemoteJmsConfigFactory {

    private static final String FACTORY = "ConnectionFactory";

    private RemoteJmsConfigFactory() {
        // just hide the constructor
    }

    public static JmsConfig getRemoteJmsConfig(String providerUrl, String queueName,
            String queueUser, String queuePass, JmsSenderFactoryType type) {
        return JmsConfig.getJmsConfig(FACTORY, queueName, queueUser, queuePass, false,
                createContextProperties(providerUrl), Collections.<String, Object> emptyMap(), type);
    }

    private static Properties createContextProperties(String providerUrl) {
        Properties properties = new Properties();
        properties.put("java.naming.factory.initial", "org.jnp.interfaces.NamingContextFactory");
        properties.put("java.naming.factory.url.pkgs", "org.jboss.naming:org.jnp.interface");
        properties.put("java.naming.provider.url", providerUrl);
        return properties;
    }
}
