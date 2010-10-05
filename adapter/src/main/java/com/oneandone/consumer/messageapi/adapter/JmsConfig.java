package com.oneandone.consumer.messageapi.adapter;

import java.util.Map;
import java.util.Properties;

import javax.naming.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import com.google.common.base.Supplier;

/**
 * The configuration container for the {@link AbstractJmsSenderFactory}. Although this class
 * <b>can</b> be instantiated directly, most commonly a factory like {@link DefaultJmsConfigFactory}
 * is used.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public final class JmsConfig {

    private final String factoryName;
    private final String queueName;
    private final String user;
    private final String pass;
    private final boolean transacted;
    private final Supplier<Properties> contextPropertiesSupplier;
    private final Supplier<Map<String, Object>> headerSupplier;

    // just to satisfy JAXB
    @SuppressWarnings("unused")
    private JmsConfig() {
        this.factoryName = null;
        this.queueName = null;
        this.user = null;
        this.pass = null;
        this.transacted = true;
        this.contextPropertiesSupplier = null;
        this.headerSupplier = null;
    }

    public JmsConfig(String factoryName, String queueName, String user, String pass,
            boolean transacted, Supplier<Properties> contextPropertiesSupplier,
            Supplier<Map<String, Object>> headerSupplier) {
        this.factoryName = factoryName;
        this.queueName = queueName;
        this.user = user;
        this.pass = pass;
        this.transacted = transacted;
        this.contextPropertiesSupplier = contextPropertiesSupplier;
        this.headerSupplier = headerSupplier;
    }

    public String getFactoryName() {
        return factoryName;
    }

    public String getQueueName() {
        return queueName;
    }

    public String getUser() {
        return user;
    }

    public String getPass() {
        return pass;
    }

    public boolean isTransacted() {
        return transacted;
    }

    public Context getContext() throws NamingException {
        return new InitialContext(contextPropertiesSupplier.get());
    }

    public Map<String, Object> getAdditionalProperties() {
        return headerSupplier.get();
    }

}
