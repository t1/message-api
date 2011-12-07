package net.java.messageapi.adapter;

import static org.junit.Assert.*;
import net.java.messageapi.*;
import net.java.messageapi.test.JmsPropertyApi;

import org.junit.Test;

public class MessageApiBeanIntegrationTest {

    @MessageApi
    @JmsMappedPayload
    public static interface ConfiguredTestApi {
        // must be arg0, so it runs without the api annotation processor as well as with it
        public void configuredMethod(@JmsMappedName("aarg") String arg0);
    }

    @Test
    public void shouldMapArgument() throws Exception {
        MessageApiBean<ConfiguredTestApi> bean = MessageApiBean.of(ConfiguredTestApi.class);

        MapJmsPayloadHandler handler = (MapJmsPayloadHandler) bean.factory.getPayloadHandler();
        assertEquals("aarg", handler.mapping.getMappingForField("arg0").getAttributeName());
    }

    @Test
    public void shouldCreatePropertyPayloadForJmsPropertyApi() throws Exception {
        MessageApiBean<JmsPropertyApi> bean = MessageApiBean.of(JmsPropertyApi.class);
        XmlJmsPayloadHandler handler = (XmlJmsPayloadHandler) bean.factory.getPayloadHandler();
        Object pojo = Class.forName("net.java.messageapi.test.JmsPropertyApi$JmsPropertyMethod").getConstructor(
                String.class, String.class).newInstance("first", "second");

        String payload = handler.toPayload(pojo);

        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" //
                + "<jmsPropertyMethod>\n" //
                + "    <two>second</two>\n" //
                + "</jmsPropertyMethod>\n", payload);
    }
}
