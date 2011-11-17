package net.java.messageapi.adapter;

import javax.jms.JMSException;
import javax.jms.Message;

class VersionSupplier implements JmsHeaderSupplier {
    String getVersion(Class<?> api) {
        String version = api.getPackage().getSpecificationVersion();
        if (version == null)
            version = api.getPackage().getImplementationVersion();
        return version;
    }

    @Override
    public void addTo(Message message, Object pojo) throws JMSException {
        String version = getVersion(pojo.getClass());
        if (version != null) {
            message.setStringProperty("VERSION", version);
        }
    }

    @Override
    public boolean equals(Object other) {
        return other != null && other.getClass().equals(this.getClass());
    }

    @Override
    public int hashCode() {
        return 1; // that's okay... all VersionSuppliers are equal
    }
}
