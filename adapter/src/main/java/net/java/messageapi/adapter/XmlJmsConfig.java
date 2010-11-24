package net.java.messageapi.adapter;

import java.util.Map;
import java.util.Properties;

import javax.xml.bind.annotation.XmlRootElement;

import net.java.messageapi.adapter.xml.JmsXmlSenderFactory;

@XmlRootElement
public class XmlJmsConfig extends JmsConfig {

    // just to satisfy JAXB
    @SuppressWarnings("unused")
    private XmlJmsConfig() {
    }

    public XmlJmsConfig(String factoryName, String destinationName, String user, String pass,
            boolean transacted, Properties contextProperties, Map<String, Object> header) {
        super(factoryName, destinationName, user, pass, transacted, contextProperties, header);
    }

    @Override
    public <T> AbstractJmsSenderFactory<T, ?> createFactory(Class<T> api) {
        return new JmsXmlSenderFactory<T>(api, this);
    }
}
