package net.java.messageapi.adapter;

import static net.sf.twip.verify.Verify.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.lang.reflect.Method;

import net.java.messageapi.*;
import net.java.messageapi.adapter.mapped.MapJmsPayloadHandler;
import net.java.messageapi.adapter.xml.XmlJmsPayloadHandler;
import net.java.messageapi.test.JmsPropertyApi;
import net.sf.twip.TwiP;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(TwiP.class)
public class MessageApiBeanIntegrationTest {

    @MessageApi
    @JmsPayloadMapping
    public static interface ConfiguredTestApi {
        public void configuredMethod(@JmsMappedName("aarg") String someArg);
    }

    @Test
    public void shouldMapArgument() throws Exception {
        MessageApiBean<ConfiguredTestApi> bean = MessageApiBean.of(ConfiguredTestApi.class);

        MapJmsPayloadHandler handler = (MapJmsPayloadHandler) bean.factory.getPayloadHandler();
        verifyThat(handler.mapping.getMappingForField("someArg").getAttributeName(), is("aarg"));
    }

    @Test
    public void shouldCreatePropertyPayload() throws Exception {
        MessageApiBean<JmsPropertyApi> bean = MessageApiBean.of(JmsPropertyApi.class);
        XmlJmsPayloadHandler handler = (XmlJmsPayloadHandler) bean.factory.getPayloadHandler();
        Method method = JmsPropertyApi.class.getMethod("jmsPropertyMethod", new Class[] {
                String.class, String.class });
        Object pojo = Class.forName("net.java.messageapi.test.JmsPropertyMethod").getConstructor(
                String.class, String.class).newInstance("first", "second");

        String payload = handler.toPayload(JmsPropertyApi.class, method, pojo);

        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" //
                + "<jmsPropertyMethod>\n" //
                + "    <one>first</one>\n" //
                + "    <two>second</two>\n" //
                + "</jmsPropertyMethod>\n", payload);
    }
}