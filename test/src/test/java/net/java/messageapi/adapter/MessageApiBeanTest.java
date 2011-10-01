package net.java.messageapi.adapter;

import static net.sf.twip.verify.Verify.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

import net.java.messageapi.*;
import net.java.messageapi.adapter.mapped.MapJmsPayloadHandler;
import net.java.messageapi.adapter.xml.XmlJmsPayloadHandler;
import net.sf.twip.TwiP;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.ImmutableMap;

@RunWith(TwiP.class)
public class MessageApiBeanTest {

    private static final Map<String, Object> EMPTY_PROPS = ImmutableMap.of();

    @MessageApi
    public static interface UnconfiguredTestApi {
        public void unconfiguredTestMethod();
    }

    @Test
    public void shouldCreateDefaultConfig() throws Exception {
        MessageApiBean<UnconfiguredTestApi> bean = new MessageApiBean<UnconfiguredTestApi>(
                UnconfiguredTestApi.class, Collections.<Annotation> emptySet());
        JmsQueueConfig config = bean.factory.getConfig();

        verifyThat(config.getFactoryName(), is(ConnectionFactoryName.DEFAULT));
        verifyThat(config.getDestinationName(), is(UnconfiguredTestApi.class.getCanonicalName()));
        verifyThat(config.getUser(), is(nullValue()));
        verifyThat(config.getPass(), is(nullValue()));
        verifyThat(config.isTransacted(), is(true));
        verifyThat(config.getContextProperties(), is(new Properties()));
        verifyThat(config.getAdditionalProperties(), is(EMPTY_PROPS));
    }

    @Test
    public void shouldCreateXmlPayloadHandler() throws Exception {
        MessageApiBean<UnconfiguredTestApi> bean = new MessageApiBean<UnconfiguredTestApi>(
                UnconfiguredTestApi.class, Collections.<Annotation> emptySet());

        verifyThat(bean.factory.getPayloadHandler(), instanceOf(XmlJmsPayloadHandler.class));
    }

    @MessageApi
    // @DestinationName("testination")
    @JmsPayloadMapping(operationName = "op")
    public static interface ConfiguredTestApi {
        public void configuredMethod(@JmsMappedName("aarg") String someArg);
    }

    @Test
    public void shouldCreateAnnotatedConfig() throws Exception {
        MessageApiBean<ConfiguredTestApi> bean = new MessageApiBean<ConfiguredTestApi>(
                ConfiguredTestApi.class, Collections.<Annotation> emptySet());
        JmsQueueConfig config = bean.factory.getConfig();

        verifyThat(config.getFactoryName(), is(ConnectionFactoryName.DEFAULT));
        verifyThat(config.getDestinationName(), is(ConfiguredTestApi.class.getCanonicalName()));
        verifyThat(config.getUser(), is(nullValue()));
        verifyThat(config.getPass(), is(nullValue()));
        verifyThat(config.isTransacted(), is(true));
        verifyThat(config.getContextProperties(), is(new Properties()));
        verifyThat(config.getAdditionalProperties(), is(EMPTY_PROPS));
    }

    @Test
    public void shouldCreateMapPayloadHandler() throws Exception {
        MessageApiBean<ConfiguredTestApi> bean = new MessageApiBean<ConfiguredTestApi>(
                ConfiguredTestApi.class, Collections.<Annotation> emptySet());

        verifyThat(bean.factory.getPayloadHandler(), instanceOf(MapJmsPayloadHandler.class));
    }

    @Test
    public void shouldCreateOperationName() throws Exception {
        MessageApiBean<ConfiguredTestApi> bean = new MessageApiBean<ConfiguredTestApi>(
                ConfiguredTestApi.class, Collections.<Annotation> emptySet());

        MapJmsPayloadHandler handler = (MapJmsPayloadHandler) bean.factory.getPayloadHandler();
        verifyThat(handler.mapping.getOperationMessageAttibute(), is("op"));
        verifyThat(handler.mapping.getMappingForField("someArg").getAttributeName(), is("aarg"));
    }

    @MessageApi
    public static interface JmsPropertyTestApi {
        public void propertyTestMethod(@JmsProperty() String first, String second);
    }

    @Test
    public void shouldCreateProperty() throws Exception {
        MessageApiBean<JmsPropertyTestApi> bean = new MessageApiBean<JmsPropertyTestApi>(
                JmsPropertyTestApi.class, Collections.<Annotation> emptySet());
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
