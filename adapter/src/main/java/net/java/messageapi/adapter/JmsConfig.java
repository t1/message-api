package net.java.messageapi.adapter;

import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import javax.naming.*;
import javax.xml.bind.*;
import javax.xml.bind.annotation.*;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;

/**
 * The configuration container for the {@link AbstractJmsSenderFactory}. Although this class
 * <b>can</b> be instantiated directly, most commonly a factory like {@link DefaultJmsConfigFactory}
 * is used.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public final class JmsConfig {

    /**
     * Load a {@link JmsConfig} from a file named like that interface plus "-jmsconfig.xml"
     */
    public static JmsConfig getConfigFor(Class<?> api) {
        InputStream stream = getStreamFor(api);
        return readConfigFrom(stream);
    }

    private static InputStream getStreamFor(Class<?> api) {
        String fileName = api.getSimpleName() + "-jmsconfig.xml";
        InputStream stream = api.getResourceAsStream(fileName);
        if (stream == null)
            throw new RuntimeException("file not found: " + fileName);
        return stream;
    }

    public static JmsConfig readConfigFrom(InputStream stream) {
        try {
            JAXBContext context = JAXBContext.newInstance(JmsConfig.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return (JmsConfig) unmarshaller.unmarshal(stream);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    @XmlElement(name = "factory")
    private final String factoryName;
    @XmlElement(name = "destination")
    private final String destinationName;
    private final String user;
    private final String pass;
    private final boolean transacted;
    private final JmsSenderFactoryType senderType;

    // FIXME these should not be transient
    @XmlTransient
    private final Supplier<Properties> contextPropertiesSupplier;
    @XmlTransient
    private final Supplier<Map<String, Object>> headerSupplier;

    // just to satisfy JAXB
    @SuppressWarnings("unused")
    private JmsConfig() {
        this.factoryName = null;
        this.destinationName = null;
        this.user = null;
        this.pass = null;
        this.transacted = true;
        this.contextPropertiesSupplier = null;
        this.headerSupplier = null;
        this.senderType = null;
    }

    public JmsConfig(String factoryName, String destinationName, String user, String pass,
            boolean transacted, Supplier<Properties> contextPropertiesSupplier,
            Supplier<Map<String, Object>> headerSupplier, JmsSenderFactoryType senderType) {
        this.factoryName = factoryName;
        this.destinationName = destinationName;
        this.user = user;
        this.pass = pass;
        this.transacted = transacted;
        this.contextPropertiesSupplier = contextPropertiesSupplier;
        this.headerSupplier = headerSupplier;
        this.senderType = senderType;
    }

    public String getFactoryName() {
        return factoryName;
    }

    public String getDestinationName() {
        return destinationName;
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
        Properties context = (contextPropertiesSupplier == null) ? new Properties()
                : contextPropertiesSupplier.get();
        return new InitialContext(context);
    }

    public Map<String, Object> getAdditionalProperties() {
        if (headerSupplier == null)
            return ImmutableMap.of();
        return headerSupplier.get();
    }

    public JmsSenderFactoryType getSenderType() {
        return senderType;
    }

    public <T> AbstractJmsSenderFactory<T, ?> createFactory(Class<T> api) {
        return senderType.createFactory(api, this);
    }

    public <T> T createProxy(Class<T> api) {
        return createFactory(api).get();
    }
}
