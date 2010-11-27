package net.java.messageapi.adapter;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;

import javax.naming.*;
import javax.xml.bind.*;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.google.common.collect.ImmutableMap;

/**
 * The configuration container for the {@link AbstractJmsSenderFactory}. Although this class
 * <b>can</b> be instantiated directly, most commonly a factory like {@link DefaultJmsConfigFactory}
 * is used.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso( { XmlJmsConfig.class, MapJmsConfig.class })
public abstract class JmsConfig {

    private static final String CONFIG_FILE_SUFFIX = "-jmsconfig.xml";
    private static final String DEFAULT_FILE_NAME = "default" + CONFIG_FILE_SUFFIX;

    /**
     * Load a {@link JmsConfig} from a file named like that interface plus "-jmsconfig.xml"
     */
    public static JmsConfig getConfigFor(Class<?> api) {
        Reader reader = getReaderFor(api);
        return readConfigFrom(reader);
    }

    private static Reader getReaderFor(Class<?> api) {
        String fileName = api.getName() + CONFIG_FILE_SUFFIX;
        InputStream stream = getSingleUrlFor(fileName);
        if (stream == null)
            stream = getSingleUrlFor(DEFAULT_FILE_NAME);
        if (stream == null)
            throw new RuntimeException("found no config file [" + fileName + "]");
        return new InputStreamReader(stream, Charset.forName("utf-8"));
    }

    private static InputStream getSingleUrlFor(String fileName) {
        try {
            Enumeration<URL> resources = ClassLoader.getSystemResources(fileName);
            if (!resources.hasMoreElements())
                return null;
            URL result = resources.nextElement();
            if (resources.hasMoreElements())
                throw new RuntimeException("found multiple configs files [" + fileName + "]");
            return result.openStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static JmsConfig readConfigFrom(Reader reader) {
        try {
            JAXBContext context = JAXBContext.newInstance(JmsConfig.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return (JmsConfig) unmarshaller.unmarshal(reader);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeConfigTo(Writer writer) {
        JAXB.marshal(this, writer);
    }

    public static JmsConfig getJmsConfig(String factoryName, String queueName, String user,
            String pass, boolean transacted, Properties contextProperties,
            Map<String, Object> header, JmsSenderFactoryType type) {
        // FIXME
        if (type == JmsSenderFactoryType.XML) {
            return new XmlJmsConfig(factoryName, queueName, user, pass, transacted,
                    contextProperties, header);
        } else if (type == JmsSenderFactoryType.MAP) {
            return new MapJmsConfig(factoryName, queueName, user, pass, transacted,
                    contextProperties, header);
        } else {
            throw new UnsupportedOperationException("unknown type: " + type);
        }
    }

    @XmlElement(name = "factory")
    private final String factoryName;
    @XmlElement(name = "destination")
    private final String destinationName;
    private final String user;
    private final String pass;
    private final boolean transacted;

    @XmlJavaTypeAdapter(PropertiesMapAdapter.class)
    private final Properties contextProperties;
    private final Map<String, Object> header;

    // just to satisfy JAXB
    protected JmsConfig() {
        this.factoryName = null;
        this.destinationName = null;
        this.user = null;
        this.pass = null;
        this.transacted = true;
        this.contextProperties = null;
        this.header = null;
    }

    public JmsConfig(String factoryName, String destinationName, String user, String pass,
            boolean transacted, Properties contextProperties, Map<String, Object> header) {
        this.factoryName = factoryName;
        this.destinationName = destinationName;
        this.user = user;
        this.pass = pass;
        this.transacted = transacted;
        this.contextProperties = contextProperties;
        this.header = header;
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

    public Properties getContextProperties() {
        return (contextProperties == null) ? new Properties() : contextProperties;
    }

    public Context getContext() throws NamingException {
        return new InitialContext(getContextProperties());
    }

    public Map<String, Object> getAdditionalProperties() {
        if (header == null)
            return ImmutableMap.of();
        return header;
    }

    public <T> T createProxy(Class<T> api) {
        return createFactory(api).get();
    }

    public abstract <T> AbstractJmsSenderFactory<T, ?> createFactory(Class<T> api);

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((contextProperties == null) ? 0 : contextProperties.hashCode());
        result = prime * result + ((destinationName == null) ? 0 : destinationName.hashCode());
        result = prime * result + ((factoryName == null) ? 0 : factoryName.hashCode());
        result = prime * result + ((header == null) ? 0 : header.hashCode());
        result = prime * result + ((pass == null) ? 0 : pass.hashCode());
        result = prime * result + (transacted ? 1231 : 1237);
        result = prime * result + ((user == null) ? 0 : user.hashCode());
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
        JmsConfig other = (JmsConfig) obj;
        if (contextProperties == null) {
            if (other.contextProperties != null) {
                return false;
            }
        } else if (!contextProperties.equals(other.contextProperties)) {
            return false;
        }
        if (destinationName == null) {
            if (other.destinationName != null) {
                return false;
            }
        } else if (!destinationName.equals(other.destinationName)) {
            return false;
        }
        if (factoryName == null) {
            if (other.factoryName != null) {
                return false;
            }
        } else if (!factoryName.equals(other.factoryName)) {
            return false;
        }
        if (header == null) {
            if (other.header != null) {
                return false;
            }
        } else if (!header.equals(other.header)) {
            return false;
        }
        if (pass == null) {
            if (other.pass != null) {
                return false;
            }
        } else if (!pass.equals(other.pass)) {
            return false;
        }
        if (transacted != other.transacted) {
            return false;
        }
        if (user == null) {
            if (other.user != null) {
                return false;
            }
        } else if (!user.equals(other.user)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "JmsConfig ["
                + (contextProperties != null ? "contextProperties=" + contextProperties + ", " : "")
                + (destinationName != null ? "destinationName=" + destinationName + ", " : "")
                + (factoryName != null ? "factoryName=" + factoryName + ", " : "")
                + (header != null ? "header=" + header + ", " : "")
                + (pass != null ? "pass=" + pass + ", " : "") + "transacted=" + transacted + ", "
                + (user != null ? "user=" + user : "") + "]";
    }
}
