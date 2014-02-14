package net.java.messageapi.adapter;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.*;

import javax.enterprise.util.AnnotationLiteral;

import net.java.messageapi.*;

import org.junit.Test;

public class MessageApiBeanTest {

    @SuppressWarnings("all")
    private static class ConnectionFactoryNameBinding extends AnnotationLiteral<ConnectionFactoryName> implements
            ConnectionFactoryName {
        private static final long serialVersionUID = 1L;
        private final String factoryName;

        public ConnectionFactoryNameBinding(String factoryName) {
            this.factoryName = factoryName;
        }

        @Override
        public String value() {
            return factoryName;
        }
    }

    @SuppressWarnings("all")
    private static class DestinationNameBinding extends AnnotationLiteral<DestinationName> implements DestinationName {

        private final String destinationName;

        public DestinationNameBinding(String destinationName) {
            this.destinationName = destinationName;
        }

        @Override
        public String value() {
            return destinationName;
        }
    }

    private static final Map<String, Object> EMPTY_PROPS = Collections.emptyMap();

    @MessageApi
    public static interface DummyApi {
        public void unconfiguredTestMethod();
    }

    @Test
    public void shouldCreateDefaultConfig() throws Exception {
        MessageApiBean<DummyApi> bean = MessageApiBean.of(DummyApi.class);

        assertEquals(DummyApi.class, bean.getBeanClass());

        JmsQueueConfig config = bean.factory.getConfig();

        assertThat(config.getFactoryName(), is(ConnectionFactoryName.DEFAULT));
        assertThat(config.getDestinationName(), is(DummyApi.class.getCanonicalName()));
        assertThat(config.getUser(), is(nullValue()));
        assertThat(config.getPass(), is(nullValue()));
        assertThat(config.isTransacted(), is(false));
        assertThat(config.getContextProperties(), is(new Properties()));
        assertThat(config.getAdditionalProperties(), is(EMPTY_PROPS));
    }

    @Test
    public void shouldCreateXmlPayloadHandler() throws Exception {
        MessageApiBean<DummyApi> bean = MessageApiBean.of(DummyApi.class);

        assertThat(bean.factory.getPayloadHandler(), instanceOf(XmlJmsPayloadHandler.class));
    }

    @Test
    public void shouldRecognizeConnectionFactoryNameAnnotation() throws Exception {
        ConnectionFactoryNameBinding binding = new ConnectionFactoryNameBinding("foo");
        MessageApiBean<DummyApi> bean = MessageApiBean.of(DummyApi.class, binding);
        JmsQueueConfig config = bean.factory.getConfig();

        assertEquals("foo", config.getFactoryName());
    }

    @Test
    public void shouldRecognizeDestinationNameAnnotation() throws Exception {
        MessageApiBean<DummyApi> bean = MessageApiBean.of(DummyApi.class, new DestinationNameBinding("bar"));
        JmsQueueConfig config = bean.factory.getConfig();

        assertEquals("bar", config.getDestinationName());
    }

    @MessageApi
    @JmsMappedPayload(operationName = "op")
    public static interface MappedDummyApi {
        public void unconfiguredTestMethod(@JmsName("aarg") String arg0);
    }

    @Test
    public void shouldRecognizeMappedOperationName() throws Exception {
        MessageApiBean<MappedDummyApi> bean = MessageApiBean.of(MappedDummyApi.class);

        MapJmsPayloadHandler payloadHandler = (MapJmsPayloadHandler) bean.factory.getPayloadHandler();
        assertEquals("op", payloadHandler.mapping.getOperationMessageAttibute());
    }

    @Test
    public void shouldRecognizeMappedArgumentName() throws Exception {
        MessageApiBean<MappedDummyApi> bean = MessageApiBean.of(MappedDummyApi.class);

        MapJmsPayloadHandler handler = (MapJmsPayloadHandler) bean.factory.getPayloadHandler();
        assertEquals("aarg", handler.mapping.getMappingForField("aarg").getAttributeName());
    }
}
