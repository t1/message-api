package net.java.messageapi.test;

import static org.junit.Assert.*;

import java.util.Properties;

import net.java.messageapi.adapter.*;
import net.java.messageapi.adapter.xml.JaxbProvider;
import net.java.messageapi.adapter.xml.JaxbProvider.JaxbProviderMemento;
import net.sf.twip.*;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(TwiP.class)
public class RemoteJmsConfigFactoryTest {

    private final JaxbProviderMemento memento;

    public RemoteJmsConfigFactoryTest(@NotNull @Assume("!= XSTREAM") JaxbProvider jaxb) {
        memento = jaxb.setUp();
    }

    @After
    public void cleanup() {
        memento.restore();
    }

    @Test
    public void loadRemoteConfig() throws Exception {
        JmsConfig config = JmsConfig.getConfigFor(RemoteConfigApi.class);
        verify(config);
    }

    @Test
    public void loadXmlConfig() throws Exception {
        JmsConfig config = RemoteJmsConfigFactory.getRemoteJmsConfig("provider", "queue", "user",
                "pass", JmsSenderFactoryType.XML);
        // JAXB.marshal(config, System.out);
        verify(config);
    }

    @Test
    public void loadMapConfig() throws Exception {
        JmsConfig config = RemoteJmsConfigFactory.getRemoteJmsConfig("provider", "queue", "user",
                "pass", JmsSenderFactoryType.MAP);
        verify(config);
    }

    private void verify(JmsConfig config) {
        assertEquals("ConnectionFactory", config.getFactoryName());
        assertEquals("queue", config.getDestinationName());
        assertEquals("user", config.getUser());
        assertEquals("pass", config.getPass());
        assertEquals(false, config.isTransacted());
        assertEquals(0, config.getAdditionalProperties().size());
        Properties props = config.getContextProperties();
        assertEquals("provider", props.get("java.naming.provider.url"));
        assertEquals("org.jnp.interfaces.NamingContextFactory",
                props.get("java.naming.factory.initial"));
        assertEquals("org.jboss.naming:org.jnp.interface",
                props.get("java.naming.factory.url.pkgs"));
    }
}
