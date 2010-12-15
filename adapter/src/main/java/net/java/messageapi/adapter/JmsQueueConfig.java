package net.java.messageapi.adapter;

import java.util.Map;
import java.util.Properties;

import javax.naming.*;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.google.common.collect.ImmutableMap;

@XmlAccessorType(XmlAccessType.FIELD)
public class JmsQueueConfig {

    @XmlElement(name = "factory")
    private final String factoryName;
    @XmlAttribute(name = "name")
    private final String destinationName;
    private final String user;
    private final String pass;
    private final boolean transacted;

    @XmlJavaTypeAdapter(PropertiesMapAdapter.class)
    private final Properties contextProperties;
    @XmlJavaTypeAdapter(MapAdapter.class)
    private final Map<String, Object> header;

    // just to satisfy JAXB
    protected JmsQueueConfig() {
        this.factoryName = null;
        this.destinationName = null;
        this.user = null;
        this.pass = null;
        this.transacted = true;
        this.contextProperties = null;
        this.header = null;
    }

    public JmsQueueConfig(String factoryName, String destinationName, String user, String pass,
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
        JmsQueueConfig other = (JmsQueueConfig) obj;
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
        return "JmsConfig [" //
                + ("factoryName=" + factoryName + ", ")
                + ("destinationName=" + destinationName + ", ")
                + ("user=" + user + ", ")
                + ("pass=" + pass + ", ")
                + ("transacted=" + transacted + ", ")
                + (contextProperties != null ? "context=" + contextProperties : "")
                + (header != null ? "header=" + header : "") + "]";
    }
}
