package net.java.messageapi.adapter;

import javax.jms.JMSException;
import javax.jms.Message;

class VersionSupplier implements JmsHeaderSupplier {
    private String getValue(Class<?> api) {
        String version = api.getPackage().getSpecificationVersion();
        if (version == null)
            version = api.getPackage().getImplementationVersion();
        if (version == null)
            version = "?";
        return version;
    }

    @Override
    public void addTo(Message message, Object pojo) throws JMSException {
        message.setStringProperty("VERSION", getValue(pojo.getClass()));
    }

    @Override
    public boolean equals(Object other) {
        return other != null && other.getClass().equals(this.getClass());
    }
}
