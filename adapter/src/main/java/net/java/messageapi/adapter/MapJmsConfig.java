package net.java.messageapi.adapter;

import java.util.Map;
import java.util.Properties;

import javax.xml.bind.annotation.XmlRootElement;

import net.java.messageapi.adapter.mapped.JmsMapSenderFactory;

import com.google.common.base.Supplier;

@XmlRootElement
public class MapJmsConfig extends JmsConfig {

    // just to satisfy JAXB
    @SuppressWarnings("unused")
    private MapJmsConfig() {
    }

    public MapJmsConfig(String factoryName, String destinationName, String user, String pass,
            boolean transacted, Supplier<Properties> contextPropertiesSupplier,
            Supplier<Map<String, Object>> headerSupplier) {
        super(factoryName, destinationName, user, pass, transacted, contextPropertiesSupplier,
                headerSupplier);
    }

    @Override
    public <T> AbstractJmsSenderFactory<T, ?> createFactory(Class<T> api) {
        return new JmsMapSenderFactory<T>(api, this);
    }
}
