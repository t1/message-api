package net.java.messageapi.test;

import static org.junit.Assert.*;

import java.io.StringReader;
import java.io.StringWriter;

import net.java.messageapi.adapter.JmsConfig;
import net.java.messageapi.adapter.XmlJmsConfig;
import net.java.messageapi.adapter.xml.JaxbProvider;
import net.java.messageapi.adapter.xml.JaxbProvider.JaxbProviderMemento;
import net.sf.twip.*;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(TwiP.class)
public class JmsConfigTest {

    private final JaxbProviderMemento memento;

    public JmsConfigTest(@NotNull @Assume("!= XSTREAM") JaxbProvider jaxb) {
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
}
