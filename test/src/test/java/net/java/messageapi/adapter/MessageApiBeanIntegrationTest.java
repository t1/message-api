package net.java.messageapi.adapter;

import static net.sf.twip.verify.Verify.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.lang.reflect.Method;

import net.java.messageapi.*;
import net.java.messageapi.adapter.mapped.MapJmsPayloadHandler;
import net.java.messageapi.adapter.xml.XmlJmsPayloadHandler;
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

    @MessageApi
    public static interface JmsPropertyTestApi {
        public void propertyTestMethod(@JmsProperty() String first, String second);
    }

    @Test
    public void shouldCreatePropertyPayload() throws Exception {
        MessageApiBean<JmsPropertyTestApi> bean = MessageApiBean.of(JmsPropertyTestApi.class);
        XmlJmsPayloadHandler handler = (XmlJmsPayloadHandler) bean.factory.getPayloadHandler();
        Method method = JmsPropertyTestApi.class.getMethod("propertyTestMethod", new Class[] {
                String.class, String.class });

        String payload = handler.toPayload(JmsPropertyTestApi.class, method, new Object[] {
                "first", "second" });

        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" //
                + "<propertyTestMethod>\n" //
                + "    <first>first</first>\n" //
                + "    <second>second</second>\n" //
                + "</propertyTestMethod>\n", payload);
    }
}
