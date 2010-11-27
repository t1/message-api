package net.java.messageapi.test;

import static org.junit.Assert.*;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Properties;

import net.java.messageapi.adapter.JmsConfig;
import net.java.messageapi.adapter.XmlJmsConfig;
import net.java.messageapi.adapter.xml.JaxbProvider;
import net.java.messageapi.adapter.xml.JaxbProvider.JaxbProviderMemento;
import net.sf.twip.*;
import net.sf.twip.Assume;

import org.junit.*;
import org.junit.runner.RunWith;

@RunWith(TwiP.class)
public class JmsConfigTest {

    private final JaxbProviderMemento memento;

    // FIXME support ECLIPSE_LINK
    public JmsConfigTest(@NotNull @Assume("!= XSTREAM & != ECLIPSE_LINK") JaxbProvider jaxb) {
        memento = jaxb.setUp();
    }

    @After
    public void cleanup() {
        memento.restore();
    }

    @Test
    public void shouldConvertAndBack() throws Exception {
        // given
        JmsConfig configIn = new XmlJmsConfig("factory", "destination", "user", "pass", true, null,
                null);

        // when
        StringWriter writer = new StringWriter();
        configIn.writeConfigTo(writer);
        JmsConfig configOut = JmsConfig.readConfigFrom(new StringReader(writer.toString()));

        // then
        assertEquals(configIn, configOut);
    }

    @Test
    public void loadRemoteConfig() throws Exception {
        JmsConfig config = JmsConfig.getConfigFor(RemoteConfigApi.class);

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

    @Test
    public void findDefaultConfigFile() {
        JmsConfig config = JmsConfig.getConfigFor(DefaultConfigApi.class);
        assertEquals("defaultQueue", config.getDestinationName());
    }

    @Test
    @Ignore("we have a default-xmlconfig.xml... maybe we need a sub-module?")
    public void failWithoutConfigFile() {
        try {
            JmsConfig.getConfigFor(NoConfigApi.class);
            fail("RuntimeException expected");
        } catch (RuntimeException e) {
            assertEquals(
                    "found no config file [net.java.messageapi.test.NoConfigApi-jmsconfig.xml]",
                    e.getMessage());
        }
    }

    @Test
    @Ignore("how do I get the same resource twice? maybe a sub-module?")
    public void failWithDoubleConfigFile() {
        try {
            JmsConfig.getConfigFor(DoubleConfigApi.class);
            fail("RuntimeException expected");
        } catch (RuntimeException e) {
            assertEquals(
                    "found multiple config files [net.java.messageapi.test.DoubleConfigApi-jmsconfig.xml]",
                    e.getMessage());
        }
    }
}
