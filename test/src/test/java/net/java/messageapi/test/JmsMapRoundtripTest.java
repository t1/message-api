package net.java.messageapi.test;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

import javax.jms.*;

import net.java.messageapi.adapter.*;
import net.java.messageapi.converter.JodaLocalDateConverter;
import net.java.messageapi.converter.StringToBooleanConverter;
import net.java.messageapi.test.defaultjaxb.JodaTimeApi;
import net.sf.twip.TwiP;
import net.sf.twip.Values;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockejb.jms.MapMessageImpl;
import org.mockito.Mock;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

@RunWith(TwiP.class)
public class JmsMapRoundtripTest extends AbstractJmsSenderFactoryTest {

    private static final String OPERATION_FIELD_NAME = "event";

    private static final class SimpleServiceCall {
        private final String operation;
        private final String argument;
        private final Class<?> type;
        private final Object value;

        SimpleServiceCall(String operation, String argument, Class<?> type, Object value) {
            this.operation = operation;
            this.argument = argument;
            this.type = type;
            this.value = value;
        }

        @Override
        public String toString() {
            return "SimpleServiceCall [" + type + "]";
        }
    }

    public static final Collection<SimpleServiceCall> PRIMITIVE_CALLS = ImmutableList.of(
            new SimpleServiceCall("booleanCall", "b", Boolean.TYPE, false),//
            new SimpleServiceCall("byteCall", "b", Byte.TYPE, (byte) 0),//
            new SimpleServiceCall("charCall", "c", Character.TYPE, 'c'),//
            new SimpleServiceCall("shortCall", "s", Short.TYPE, (short) 0),//
            new SimpleServiceCall("intCall", "i", Integer.TYPE, 0),//
            new SimpleServiceCall("longCall", "l", Long.TYPE, 0L),//
            new SimpleServiceCall("floatCall", "f", Float.TYPE, 0.0f),//
            new SimpleServiceCall("doubleCall", "d", Double.TYPE, 0.0));

    public static final Collection<SimpleServiceCall> BOXED_PRIMITIVE_CALLS = ImmutableList.of(
            new SimpleServiceCall("boxedBooleanCall", "b", Boolean.class, false),//
            new SimpleServiceCall("boxedByteCall", "b", Byte.class, (byte) 0),//
            new SimpleServiceCall("boxedCharCall", "c", Character.class, 'c'),//
            new SimpleServiceCall("boxedShortCall", "s", Short.class, (short) 0),//
            new SimpleServiceCall("boxedIntCall", "i", Integer.class, 0),//
            new SimpleServiceCall("boxedLongCall", "l", Long.class, 0L),//
            new SimpleServiceCall("boxedFloatCall", "f", Float.class, 0.0f),//
            new SimpleServiceCall("boxedDoubleCall", "d", Double.class, 0.0));

    @Mock
    private MappedApi serviceMock;

    @Mock
    private PrimitivesTestApi primitivesServiceMock;

    @Mock
    private BoxedPrimitivesTestApi boxedPrimitivesServiceMock;

    @Test
    public void shouldCallServiceWhenSendingAsMapMessage() {
        // Given
        MappedApi sendProxy = MessageSender.of(MappedApi.class);
        Mapping receiveMapping = new MappingBuilder(OPERATION_FIELD_NAME) //
        .mapField("s1", FieldMapping.map("A")) //
        .mapField("s2", FieldMapping.map("B")) //
        .build();

        // When
        sendProxy.mappedCall("a", 0L);
        receive(captureMessage(), receiveMapping);

        // Then
        verify(serviceMock).mappedCall("a", 0L);
    }

    @Test
    public void sendShouldFailWithUnmappedName() {
        // Given
        Mapping mapping = partialMapping();
        MappedApi service = service(mapping);

        // When
        String message = null;
        try {
            service.mappedCall("a", 0L);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            message = e.getMessage();
        }

        // Then
        assertEquals("no mapping for field: s2", message);
    }

    private Mapping partialMapping() {
        Mapping mapping = mock(Mapping.class);
        given(mapping.getOperationMessageAttibute()).willReturn(OPERATION_FIELD_NAME);
        given(mapping.getOperationForMethod("mappedCall")).willReturn("OP");
        given(mapping.getMethodForOperation("OP")).willReturn("mappedCall");
        givenFieldMapping(mapping, "s1", FieldMapping.map("a"));
        return new CheckedMapping(mapping);
    }

    @Test
    public void receiveShouldFailWithUnmappedName() {
        // Given
        Mapping sendMapping = new MappingBuilder(OPERATION_FIELD_NAME).mapOperation("mappedCall",
                "OP").build();
        MappedApi service = service(sendMapping);
        service.mappedCall("a", 0L);

        Mapping receiveMapping = partialMapping();

        // When
        String message = null;
        try {
            MapMessageDecoder.create(MappedApi.class, serviceMock, receiveMapping).onMessage(
                    captureMessage());
            fail("RuntimeException expected");
        } catch (RuntimeException e) {
            message = e.getCause().getMessage();
        }

        // Then
        assertEquals("no mapping for field: s2", message);
    }

    @Test
    public void shouldMapSendAttributes() throws JMSException {
        // Given
        MappedApi proxy = MessageSender.of(MappedApi.class);

        // When
        proxy.mappedCall("a", 0L);

        // Then
        MapMessage message = (MapMessage) captureMessage();
        assertEquals("a", message.getObject("A"));
        assertEquals("0", message.getObject("B"));
    }

    @Test
    public void shouldMapReceiveAttributes() throws JMSException {
        // Given
        Mapping mapping = MessageSender.getJmsMappingFor(MappedApi.class);

        MapMessage message = createPayload(OPERATION_FIELD_NAME, "mappedCall", "A", "value1", "B",
                0L);

        // When
        receive(message, mapping);

        // Then
        verify(serviceMock).mappedCall("value1", 0L);
    }

    @Test
    public void shouldSendAttributesUsingExplicitConversion(boolean flag) throws JMSException {
        // Given
        LocalDate today = new LocalDate();
        String pattern = "dd.MM.YYYY";
        Mapping mapping = new MappingBuilder(OPERATION_FIELD_NAME) //
        .mapField("date", FieldMapping.map("date", new JodaLocalDateConverter(pattern))) //
        .mapField("flag", FieldMapping.map("flag", new StringToBooleanConverter("1", "0"))) //
        .build();
        MapJmsPayloadHandler payloadHandler = new MapJmsPayloadHandler(mapping);
        JodaTimeApi service = JmsSenderFactory.create(CONFIG, payloadHandler).create(
                JodaTimeApi.class);

        // When
        service.localDateCall(today, flag);

        // Then
        MapMessage message = (MapMessage) captureMessage();
        assertEquals(today.toString(pattern), message.getString("date"));
        assertEquals(flag ? "1" : "0", message.getString("flag"));
    }

    @Test
    public void shouldReceiveAttributesUsingExplicitConversion(boolean flag) throws JMSException {
        // Given
        String pattern = "dd.MM.YYYY";
        LocalDate today = new LocalDate();
        Mapping mapping = new MappingBuilder(OPERATION_FIELD_NAME) //
        .mapField("date", FieldMapping.map("date", new JodaLocalDateConverter(pattern))) //
        .mapField("flag", FieldMapping.map("flag", new StringToBooleanConverter("1", "0"))) //
        .build();

        MapMessage message = createPayload(OPERATION_FIELD_NAME, "localDateCall", "date",
                today.toString(pattern), "flag", flag ? "1" : "0");

        // When
        JodaTimeApi mock = mock(JodaTimeApi.class);
        MapMessageDecoder.create(JodaTimeApi.class, mock, mapping).onMessage(message);

        // Then
        verify(mock).localDateCall(today, flag);
    }

    @Test
    public void shouldMapSendOperationAttribute() throws JMSException {
        MappedApi service = service(new MappingBuilder("myop").build());

        // When
        service.mappedNoArgCall();

        // Then
        MapMessage message = (MapMessage) captureMessage();
        assertEquals("mappedNoArgCall", message.getObject("myop"));
    }

    @Test
    public void shouldMapReceiveOperationAttibute() throws JMSException {
        // Given
        MappingBuilder myMapping = new MappingBuilder("myop");

        MapMessage message = createPayload("myop", "mappedNoArgCall");

        // When
        MapMessageDecoder.create(MappedApi.class, serviceMock, myMapping.build()).onMessage(message);

        // Then
        verify(serviceMock).mappedNoArgCall();
    }

    @Test
    public void shouldMapSendOperationName() throws JMSException {
        // Given
        Mapping mapping = new MappingBuilder(OPERATION_FIELD_NAME) //
        .mapOperation("mappedNoArgCall", "MAPPED_NOARG_OP").build();
        MappedApi service = service(mapping);

        // When
        service.mappedNoArgCall();

        // Then
        MapMessage message = (MapMessage) captureMessage();
        assertEquals("MAPPED_NOARG_OP", message.getObject(OPERATION_FIELD_NAME));
    }

    @Test
    public void shouldMapReceiveOperationName() throws JMSException {
        // Given
        Mapping mapping = new MappingBuilder(OPERATION_FIELD_NAME) //
        .mapOperation("mappedNoArgCall", "MAPPED_NOARG_OP").build();

        MapMessage message = createPayload(OPERATION_FIELD_NAME, "MAPPED_NOARG_OP");

        // When
        receive(message, mapping);

        // Then
        verify(serviceMock).mappedNoArgCall();
    }

    @Test
    public void shouldCallServiceIfSendingOptionalParameter() {
        // Given
        Mapping mapping = new MappingBuilder(OPERATION_FIELD_NAME).build();
        MappedApi service = service(mapping);

        // When
        service.optionalMappedCall("optional");
        receive(captureMessage(), mapping);

        // Then
        verify(serviceMock).optionalMappedCall("optional");
    }

    @Test
    public void shouldCallServiceWithEmptyValueIfNotSendingOptionalParameter() {
        // Given
        Mapping mapping = new MappingBuilder(OPERATION_FIELD_NAME).build();
        MappedApi service = service(mapping);

        // When
        service.optionalMappedCall(null);
        receive(captureMessage(), mapping);

        // Then
        verify(serviceMock).optionalMappedCall(null);
    }

    @Test
    public void shouldCallServiceWithDefaultValueIfNotSendingOptionalParameter() {
        // Given
        Mapping mapping = new MappingBuilder(OPERATION_FIELD_NAME).mapField("string",
                FieldMapping.mapWithDefault("string", "default value")).build();
        MappedApi service = service(mapping);

        // When
        service.optionalMappedCall(null);
        receive(captureMessage(), mapping);

        // Then
        verify(serviceMock).optionalMappedCall("default value");
    }

    @Test
    public void shouldCallServiceWithDefaultValueIfNotSendingMandatoryParameter() {
        // Given
        Mapping mapping = new MappingBuilder(OPERATION_FIELD_NAME).mapField("s1",
                FieldMapping.mapWithDefault("s1", "default value")).build();
        MappedApi service = service(mapping);

        // When
        service.mappedCall(null, 1L);
        receive(captureMessage(), mapping);

        // Then
        verify(serviceMock).mappedCall("default value", 1L);
    }

    @Test
    public void shouldMapReceivePrimitiveAttributesIfWrittenAsStrings(
            @Values("PRIMITIVE_CALLS") SimpleServiceCall call) throws Exception {
        // Given
        MappingBuilder mapping = new MappingBuilder(OPERATION_FIELD_NAME);
        String operation = call.operation;
        String argument = call.argument;
        Class<?> type = call.type;
        Object value = call.value;
        mapping.mapField(argument, FieldMapping.map(argument));

        MapMessage message = createPayload(OPERATION_FIELD_NAME, operation, argument,
                String.valueOf(value));

        // When
        MapMessageDecoder.create(PrimitivesTestApi.class, primitivesServiceMock, mapping.build()).onMessage(
                message);

        // Then
        invoke(verify(primitivesServiceMock), operation, type, value);
    }

    @Test
    public void shouldMapReceiveBoxedPrimitiveAttributesIfWrittenAsStrings(
            @Values("BOXED_PRIMITIVE_CALLS") SimpleServiceCall call) throws Exception {
        // Given
        MappingBuilder mapping = new MappingBuilder(OPERATION_FIELD_NAME);
        String operation = call.operation;
        String argument = call.argument;
        Class<?> type = call.type;
        Object value = call.value;
        mapping.mapField(argument, FieldMapping.map(argument));

        MapMessage message = createPayload(OPERATION_FIELD_NAME, operation, argument,
                String.valueOf(value));

        // When
        MapMessageDecoder.create(BoxedPrimitivesTestApi.class, boxedPrimitivesServiceMock,
                mapping.build()).onMessage(message);

        // Then
        invoke(verify(boxedPrimitivesServiceMock), operation, type, value);
    }

    private void invoke(Object target, String method, Class<?> type, Object value) throws Exception {
        Method m = target.getClass().getMethod(method, type);
        m.invoke(target, value);
    }

    private MapMessage newMapMessage(Map<String, Object> body) throws JMSException {
        MapMessage message = new MapMessageImpl();
        for (String key : body.keySet()) {
            Object value = body.get(key);
            message.setObject(key, value);
        }
        return message;
    }

    private MapMessage createPayload(String key, Object value) throws JMSException {
        return newMapMessage(ImmutableMap.<String, Object> of(key, value));
    }

    private MapMessage createPayload(String key1, Object value1, String key2, Object value2)
            throws JMSException {
        return newMapMessage(ImmutableMap.<String, Object> of(key1, value1, key2, value2));
    }

    private MapMessage createPayload(String key1, Object value1, String key2, Object value2,
            String key3, Object value3) throws JMSException {
        return newMapMessage(ImmutableMap.<String, Object> of(key1, value1, key2, value2, key3,
                value3));
    }

    private MappedApi service(Mapping mapping) {
        return JmsSenderFactory.create(CONFIG, new MapJmsPayloadHandler(mapping)).create(
                MappedApi.class);
    }

    private void receive(Message message, Mapping mapping) {
        MapMessageDecoder.create(MappedApi.class, serviceMock, mapping).onMessage(message);
    }

    private <T> void givenFieldMapping(Mapping receiveMapping, String attributeName,
            FieldMapping<T> fieldMapping) {
        given((FieldMapping<T>) receiveMapping.getMappingForField(attributeName)).willReturn(
                fieldMapping);
    }
}
