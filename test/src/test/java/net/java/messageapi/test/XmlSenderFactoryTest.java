package net.java.messageapi.test;

import static org.junit.Assert.*;

import java.io.*;
import java.util.*;

import javax.xml.bind.JAXB;

import net.java.messageapi.adapter.*;
import net.sf.twip.*;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.ImmutableMap;

@RunWith(TwiP.class)
public class XmlSenderFactoryTest {

    public enum Params {
        A("factory", "destination", "user", "password", true, null, null), //
        B("factory", "destination", "user", "password", false, null, null), //
        C("f", "d", "u", "p", true, ImmutableMap.of("one", "1"), null), //
        D("f", "d", "u", "p", true, ImmutableMap.of("one", "1", "two", "2"), null), //
        E("f", "d", "u", "p", true, null, ImmutableMap.of("one", "1")), //
        F("f", "d", "u", "p", true, null, ImmutableMap.of("one", "1", "two", "2")), //
        G("f", "d", "u", "p", true, ImmutableMap.of("one", "1", "two", "2"), //
                ImmutableMap.of("three", "3", "four", "4")), //
        ;

        public final String factory;
        public final String destination;
        public final String user;
        public final String password;
        public final boolean transacted;
        public final Properties contextProperties;
        public final ImmutableMap<String, ?> header;

        Params(String factory, String destination, String user, String password, boolean transacted,
                ImmutableMap<String, String> contextProperties, ImmutableMap<String, ?> header) {
            this.factory = factory;
            this.destination = destination;
            this.user = user;
            this.password = password;
            this.transacted = transacted;
            this.contextProperties = toProperties(contextProperties);
            this.header = header;
        }

        private Properties toProperties(ImmutableMap<String, String> map) {
            if (map == null)
                return null;
            Properties properties = new Properties();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                properties.setProperty(entry.getKey(), entry.getValue());
            }
            return properties;
        }
    }

    private final JmsQueueConfig config;
    private final JmsSenderFactory factory;

    public XmlSenderFactoryTest(Params params, @NotNull JaxbProvider jaxbProvider) {
        @SuppressWarnings("unchecked")
        Map<String, Object> headerMap = (Map<String, Object>) params.header;
        this.config =
                new JmsQueueConfig(params.factory, params.destination, params.user, params.password, params.transacted,
                        params.contextProperties, headerMap);
        XmlJmsPayloadHandler payloadHandler = new XmlJmsPayloadHandler(jaxbProvider);
        this.factory = JmsSenderFactory.create(config, payloadHandler);
    }

    private String xml() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" + "<jmsSenderFactory>\n" //
                + "    <destination name=\"" + config.getDestinationName() + "\">\n" //
                + "        <factory>" + config.getFactoryName() + "</factory>\n" //
                + "        <user>" + config.getUser() + "</user>\n" //
                + "        <pass>" + config.getPass() + "</pass>\n" //
                + "        <transacted>" + config.isTransacted() + "</transacted>\n" //
                + contextXml() //
                + headerXml() //
                + "    </destination>\n" //
                + "    <xmlJmsPayloadHandler/>\n" //
                + "</jmsSenderFactory>\n";
    }

    private String contextXml() {
        if (config.getContextProperties().isEmpty())
            return "";
        StringBuilder out = new StringBuilder();
        out.append("        <contextProperties>\n");
        for (Map.Entry<Object, Object> entry : config.getContextProperties().entrySet()) {
            out.append("            <entry key=\"");
            out.append(entry.getKey());
            out.append("\">");
            out.append(entry.getValue());
            out.append("</entry>\n");
        }
        out.append("        </contextProperties>\n");
        return out.toString();
    }

    private String headerXml() {
        if (config.getAdditionalProperties().isEmpty())
            return "";
        StringBuilder out = new StringBuilder();
        out.append("        <header>\n");
        for (Map.Entry<String, Object> entry : config.getAdditionalProperties().entrySet()) {
            out.append("            <entry key=\"");
            out.append(entry.getKey());
            out.append("\">");
            out.append(entry.getValue());
            out.append("</entry>\n");
        }
        out.append("        </header>\n");
        return out.toString();
    }

    @Test
    public void shouldMarshal() throws Exception {
        StringWriter writer = new StringWriter();
        JAXB.marshal(factory, writer);

        assertEquals(xml(), writer.toString());
    }

    @Test
    public void shouldUnmarshal() throws Exception {
        StringReader reader = new StringReader(xml());
        JmsSenderFactory senderFactory = JAXB.unmarshal(reader, JmsSenderFactory.class);

        assertEquals(factory, senderFactory);
    }
}
