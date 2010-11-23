package net.java.messageapi.adapter;

import java.util.Map;
import java.util.Properties;

import net.java.messageapi.MessageApi;

import org.joda.time.Duration;
import org.joda.time.Instant;

import com.google.common.base.Suppliers;
import com.google.common.collect.Maps;

/**
 * Factory to create {@link JmsConfig JMS endpoint configurations} that uses a local temporary
 * resender queue. Thus, messages can be sent to remote queues without violating the
 * {@link MessageApi} contract. It is also possible to specify that a message should not be
 * delivered before expiration of a certain duration.
 */
public final class ResenderJmsConfigFactory {
    private static final String FACTORY = "java:/JmsXA";

    private final String resenderQueueName;
    private final String resenderQueueUser;
    private final String resenderQueuePass;

    public static ResenderJmsConfigFactory create(String resenderQueueName,
            String resenderQueueUser, String resenderQueuePass) {
        return new ResenderJmsConfigFactory(resenderQueueName, resenderQueueUser, resenderQueuePass);
    }

    private ResenderJmsConfigFactory(String resenderQueueName, String resenderQueueUser,
            String resenderQueuePass) {
        this.resenderQueueName = resenderQueueName;
        this.resenderQueueUser = resenderQueueUser;
        this.resenderQueuePass = resenderQueuePass;
    }

    public JmsConfig getDelayedJmsConfig(String queueName, String user, String pass,
            Duration delayAtLeast, JmsSenderFactoryType type) {
        return JmsConfig.getJmsConfig(FACTORY, resenderQueueName, resenderQueueUser,
                resenderQueuePass, true, Suppliers.ofInstance(new Properties()),
                Suppliers.ofInstance(createMessageProperties(queueName, user, pass, delayAtLeast)),
                type);
    }

    public JmsConfig getRemoteJmsConfig(String providerUrl, String queueName, String user,
            String pass, JmsSenderFactoryType type) {
        return getRemoteJmsConfig(providerUrl, queueName, user, pass, Duration.ZERO, type);
    }

    public JmsConfig getRemoteJmsConfig(String providerUrl, String queueName, String user,
            String pass, Duration delayAtLeast, JmsSenderFactoryType type) {
        return JmsConfig.getJmsConfig(FACTORY, resenderQueueName, resenderQueueUser,
                resenderQueuePass, true,
                Suppliers.ofInstance(createContextProperties(providerUrl)),
                Suppliers.ofInstance(createMessageProperties(queueName, user, pass, delayAtLeast)),
                type);
    }

    private static Map<String, Object> createMessageProperties(String queueName, String user,
            String pass, Duration delayAtLeast) {
        Map<String, Object> result = Maps.newHashMap();
        result.put("RESEND_DESTINATION", queueName);
        result.put("RESEND_USERNAME", user);
        result.put("RESEND_PASSWORD", pass);
        result.put("RESEND_TIME", new Instant().plus(delayAtLeast).toDate().getTime());
        return result;
    }

    private static Properties createContextProperties(String providerUrl) {
        Properties properties = new Properties();
        properties.put("java.naming.factory.initial", "org.jnp.interfaces.NamingContextFactory");
        properties.put("java.naming.factory.url.pkgs", "org.jboss.naming:org.jnp.interface");
        properties.put("java.naming.provider.url", providerUrl);
        return properties;
    }

}
