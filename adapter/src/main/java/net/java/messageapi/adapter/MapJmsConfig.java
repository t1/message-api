package net.java.messageapi.adapter;

import java.util.Map;
import java.util.Properties;

import javax.xml.bind.annotation.XmlRootElement;

import net.java.messageapi.adapter.mapped.JmsMapSenderFactory;

@XmlRootElement
public class MapJmsConfig extends JmsConfig {

    // just to satisfy JAXB
    @SuppressWarnings("unused")
    private MapJmsConfig() {
    }

    public MapJmsConfig(String factoryName, String destinationName, String user, String pass,
            boolean transacted, Properties contextProperties, Map<String, Object> header) {
        super(factoryName, destinationName, user, pass, transacted, contextProperties, header);
    }

    @Override
    public <T> AbstractJmsSenderFactory<T, ?> createFactory(Class<T> api) {
        return new JmsMapSenderFactory<T>(api, this);
    }
}
