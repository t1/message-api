package net.java.messageapi.test;

import static org.junit.Assert.*;

import java.util.Properties;

import net.java.messageapi.adapter.*;
import net.sf.twip.TwiP;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(TwiP.class)
public class RemoteJmsConfigFactoryTest {

    @Test
    public void load(JmsSenderFactoryType type) throws Exception {
        JmsConfig config = RemoteJmsConfigFactory.getRemoteJmsConfig("provider", "queue", "user",
                "pass", type);

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
