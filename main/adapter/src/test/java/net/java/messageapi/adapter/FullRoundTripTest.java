package net.java.messageapi.adapter;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.util.*;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;

import net.java.messageapi.*;
import net.sf.twip.TwiP;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import com.google.common.collect.*;

/**
 * This is actually an integration test within the adapter classes
 */
@RunWith(TwiP.class)
public class FullRoundTripTest {

    public interface FullRoundTripTestInterface {
        public void fullRoundTripMessage(String foo, @JmsProperty @Optional Integer bar);
    }

    public static class MockContextFactory implements InitialContextFactory {
        @Override
        public Context getInitialContext(Hashtable<?, ?> arg0) throws NamingException {
            return mockContext;
        }
    }

    @BeforeClass
    public static void beforeClass() {
        System.setProperty("java.naming.factory.initial", MockContextFactory.class.getName());
    }

    @AfterClass
    public static void afterClass() {
        System.clearProperty("java.naming.factory.initial");
    }

    private static final Context mockContext = mock(Context.class);

    private final FullRoundTripTestInterface sender = MessageSender.of(FullRoundTripTestInterface.class);

    @Mock
    private ConnectionFactory connectionFactory;
    @Mock
    private Connection connection;
    @Mock
    private Session session;
    @Mock
    private Destination destination;
    @Mock
    private MessageProducer messageProducer;
    @Mock
    private TextMessage message;
    @Mock
    private FullRoundTripTestInterface receiver;

    private static final String XML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
            + "<fullRoundTripMessage>\n" //
            + "    <arg0>fooo</arg0>\n" //
            + "</fullRoundTripMessage>\n";

    @Test
    public void shouldSend() throws Exception {
        when(mockContext.lookup(ConnectionFactoryName.DEFAULT)).thenReturn(connectionFactory);
        when(mockContext.lookup(FullRoundTripTestInterface.class.getName())).thenReturn(destination);

        when(connectionFactory.createConnection(null, null)).thenReturn(connection);
        when(connection.createSession(true, Session.AUTO_ACKNOWLEDGE)).thenReturn(session);
        when(session.createProducer(destination)).thenReturn(messageProducer);
        when(session.createTextMessage(anyString())).thenReturn(message);

        sender.fullRoundTripMessage("fooo", 123);

        verify(messageProducer).send(message);
        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        verify(session).createTextMessage(argument.capture());
        assertEquals(XML, argument.getValue());
        verify(message).setIntProperty("arg1", 123);
    }

    @Test
    public void shouldReceive() throws Exception {
        Object pojo = XmlStringDecoder.create(FullRoundTripTestInterface.class).decode(XML);
        setField(pojo, "arg1", 123);
        PojoInvoker.of(FullRoundTripTestInterface.class, receiver).invoke(pojo);

        verify(receiver).fullRoundTripMessage("fooo", 123);
    }

    private void setField(Object pojo, String fieldName, Object value) throws Exception {
        Field field = pojo.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(pojo, value);
    }

    public interface TestInterfaceHeaderOnlyString {
        public void testMethodHeaderOnly(@Optional String foo, @JmsProperty String bar);
    }

    @Mock
    TestInterfaceHeaderOnlyString implString;

    @Test
    public void shouldSetHeaderOnlyStringPropertyAnnotation() throws Exception {
        mockMessage();
        when(message.getStringProperty("arg1")).thenReturn("123");

        MessageDecoder<TestInterfaceHeaderOnlyString> decoder = MessageDecoder.of(
                TestInterfaceHeaderOnlyString.class, implString);

        decoder.onMessage(message);

        verify(implString).testMethodHeaderOnly("foo", "123");
    }

    protected void mockMessage() {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
                + "<testMethodHeaderOnly>\n" //
                + "    <arg0>foo</arg0>\n" //
                + "</testMethodHeaderOnly>\n";
        when(message.getText()).thenReturn(xml);
        when(message.getPropertyNames()).thenReturn(new StringTokenizer("arg1"));
    }

    public interface TestInterfaceHeaderOnlyBoolean {
        public void testMethodHeaderOnly(@Optional String foo, @JmsProperty Boolean bar);
    }

    @Mock
    TestInterfaceHeaderOnlyBoolean implBoolean;

    @Test
    public void shouldSetHeaderOnlyBooleanPropertyAnnotation() throws Exception {
        mockMessage();
        when(message.getBooleanProperty("arg1")).thenReturn(true);

        MessageDecoder<TestInterfaceHeaderOnlyBoolean> decoder = MessageDecoder.of(
                TestInterfaceHeaderOnlyBoolean.class, implBoolean);

        decoder.onMessage(message);

        verify(implBoolean).testMethodHeaderOnly("foo", true);
    }

    public interface TestInterfaceHeaderOnlyPrimitiveBoolean {
        public void testMethodHeaderOnly(@Optional String foo, @JmsProperty boolean bar);
    }

    @Mock
    TestInterfaceHeaderOnlyPrimitiveBoolean implPrimitiveBoolean;

    @Test
    public void shouldSetHeaderOnlyPrimitiveBooleanPropertyAnnotation() throws Exception {
        mockMessage();
        when(message.getBooleanProperty("arg1")).thenReturn(true);

        MessageDecoder<TestInterfaceHeaderOnlyPrimitiveBoolean> decoder = MessageDecoder.of(
                TestInterfaceHeaderOnlyPrimitiveBoolean.class, implPrimitiveBoolean);

        decoder.onMessage(message);

        verify(implPrimitiveBoolean).testMethodHeaderOnly("foo", true);
    }

    public interface TestInterfaceHeaderOnlyByte {
        public void testMethodHeaderOnly(@Optional String foo, @JmsProperty Byte bar);
    }

    @Mock
    TestInterfaceHeaderOnlyByte implByte;

    @Test
    public void shouldSetHeaderOnlyBytePropertyAnnotation() throws Exception {
        mockMessage();
        when(message.getByteProperty("arg1")).thenReturn((byte) 123);

        MessageDecoder<TestInterfaceHeaderOnlyByte> decoder = MessageDecoder.of(
                TestInterfaceHeaderOnlyByte.class, implByte);

        decoder.onMessage(message);

        verify(implByte).testMethodHeaderOnly("foo", (byte) 123);
    }

    public interface TestInterfaceHeaderOnlyPrimitiveByte {
        public void testMethodHeaderOnly(@Optional String foo, @JmsProperty byte bar);
    }

    @Mock
    TestInterfaceHeaderOnlyPrimitiveByte implPrimitiveByte;

    @Test
    public void shouldSetHeaderOnlyPrimitiveBytePropertyAnnotation() throws Exception {
        mockMessage();
        when(message.getByteProperty("arg1")).thenReturn((byte) 123);

        MessageDecoder<TestInterfaceHeaderOnlyPrimitiveByte> decoder = MessageDecoder.of(
                TestInterfaceHeaderOnlyPrimitiveByte.class, implPrimitiveByte);

        decoder.onMessage(message);

        verify(implPrimitiveByte).testMethodHeaderOnly("foo", (byte) 123);
    }

    public interface TestInterfaceHeaderOnlyShort {
        public void testMethodHeaderOnly(@Optional String foo, @JmsProperty Short bar);
    }

    @Mock
    TestInterfaceHeaderOnlyShort implShort;

    @Test
    public void shouldSetHeaderOnlyShortPropertyAnnotation() throws Exception {
        mockMessage();
        when(message.getShortProperty("arg1")).thenReturn((short) 123);

        MessageDecoder<TestInterfaceHeaderOnlyShort> decoder = MessageDecoder.of(
                TestInterfaceHeaderOnlyShort.class, implShort);

        decoder.onMessage(message);

        verify(implShort).testMethodHeaderOnly("foo", (short) 123);
    }

    public interface TestInterfaceHeaderOnlyPrimitiveShort {
        public void testMethodHeaderOnly(@Optional String foo, @JmsProperty short bar);
    }

    @Mock
    TestInterfaceHeaderOnlyPrimitiveShort implPrimitiveShort;

    @Test
    public void shouldSetHeaderOnlyPrimitiveShortPropertyAnnotation() throws Exception {
        mockMessage();
        when(message.getShortProperty("arg1")).thenReturn((short) 123);

        MessageDecoder<TestInterfaceHeaderOnlyPrimitiveShort> decoder = MessageDecoder.of(
                TestInterfaceHeaderOnlyPrimitiveShort.class, implPrimitiveShort);

        decoder.onMessage(message);

        verify(implPrimitiveShort).testMethodHeaderOnly("foo", (short) 123);
    }

    public interface TestInterfaceHeaderOnlyInteger {
        public void testMethodHeaderOnly(@Optional String foo, @JmsProperty Integer bar);
    }

    @Mock
    TestInterfaceHeaderOnlyInteger implInteger;

    @Test
    public void shouldSetHeaderOnlyIntegerPropertyAnnotation() throws Exception {
        mockMessage();
        when(message.getIntProperty("arg1")).thenReturn(123);

        MessageDecoder<TestInterfaceHeaderOnlyInteger> decoder = MessageDecoder.of(
                TestInterfaceHeaderOnlyInteger.class, implInteger);

        decoder.onMessage(message);

        verify(implInteger).testMethodHeaderOnly("foo", 123);
    }

    public interface TestInterfaceHeaderOnlyInt {
        public void testMethodHeaderOnly(@Optional String foo, @JmsProperty int bar);
    }

    @Mock
    TestInterfaceHeaderOnlyInt implInt;

    @Test
    public void shouldSetHeaderOnlyIntPropertyAnnotation() throws Exception {
        mockMessage();
        when(message.getIntProperty("arg1")).thenReturn(123);

        MessageDecoder<TestInterfaceHeaderOnlyInt> decoder = MessageDecoder.of(
                TestInterfaceHeaderOnlyInt.class, implInt);

        decoder.onMessage(message);

        verify(implInt).testMethodHeaderOnly("foo", 123);
    }

    public interface TestInterfaceHeaderOnlyLong {
        public void testMethodHeaderOnly(@Optional String foo, @JmsProperty Long bar);
    }

    @Mock
    TestInterfaceHeaderOnlyLong implLong;

    @Test
    public void shouldSetHeaderOnlyLongPropertyAnnotation() throws Exception {
        mockMessage();
        when(message.getLongProperty("arg1")).thenReturn(123L);

        MessageDecoder<TestInterfaceHeaderOnlyLong> decoder = MessageDecoder.of(
                TestInterfaceHeaderOnlyLong.class, implLong);

        decoder.onMessage(message);

        verify(implLong).testMethodHeaderOnly("foo", 123L);
    }

    public interface TestInterfaceHeaderOnlyPrimitiveLong {
        public void testMethodHeaderOnly(@Optional String foo, @JmsProperty long bar);
    }

    @Mock
    TestInterfaceHeaderOnlyPrimitiveLong implPrimitiveLong;

    @Test
    public void shouldSetHeaderOnlyPrimitiveLongPropertyAnnotation() throws Exception {
        mockMessage();
        when(message.getLongProperty("arg1")).thenReturn(123L);

        MessageDecoder<TestInterfaceHeaderOnlyPrimitiveLong> decoder = MessageDecoder.of(
                TestInterfaceHeaderOnlyPrimitiveLong.class, implPrimitiveLong);

        decoder.onMessage(message);

        verify(implPrimitiveLong).testMethodHeaderOnly("foo", 123L);
    }

    public interface TestInterfaceHeaderOnlyFloat {
        public void testMethodHeaderOnly(@Optional String foo, @JmsProperty Float bar);
    }

    @Mock
    TestInterfaceHeaderOnlyFloat implFloat;

    @Test
    public void shouldSetHeaderOnlyFloatPropertyAnnotation() throws Exception {
        mockMessage();
        when(message.getFloatProperty("arg1")).thenReturn(12.3f);

        MessageDecoder<TestInterfaceHeaderOnlyFloat> decoder = MessageDecoder.of(
                TestInterfaceHeaderOnlyFloat.class, implFloat);

        decoder.onMessage(message);

        verify(implFloat).testMethodHeaderOnly("foo", 12.3f);
    }

    public interface TestInterfaceHeaderOnlyPrimitiveFloat {
        public void testMethodHeaderOnly(@Optional String foo, @JmsProperty float bar);
    }

    @Mock
    TestInterfaceHeaderOnlyPrimitiveFloat implPrimitiveFloat;

    @Test
    public void shouldSetHeaderOnlyPrimitiveFloatPropertyAnnotation() throws Exception {
        mockMessage();
        when(message.getFloatProperty("arg1")).thenReturn(12.3f);

        MessageDecoder<TestInterfaceHeaderOnlyPrimitiveFloat> decoder = MessageDecoder.of(
                TestInterfaceHeaderOnlyPrimitiveFloat.class, implPrimitiveFloat);

        decoder.onMessage(message);

        verify(implPrimitiveFloat).testMethodHeaderOnly("foo", 12.3f);
    }

    public interface TestInterfaceHeaderOnlyDouble {
        public void testMethodHeaderOnly(@Optional String foo, @JmsProperty Double bar);
    }

    @Mock
    TestInterfaceHeaderOnlyDouble implDouble;

    @Test
    public void shouldSetHeaderOnlyDoublePropertyAnnotation() throws Exception {
        mockMessage();
        when(message.getDoubleProperty("arg1")).thenReturn(12.3);

        MessageDecoder<TestInterfaceHeaderOnlyDouble> decoder = MessageDecoder.of(
                TestInterfaceHeaderOnlyDouble.class, implDouble);

        decoder.onMessage(message);

        verify(implDouble).testMethodHeaderOnly("foo", 12.3);
    }

    public interface TestInterfaceHeaderOnlyPrimitiveDouble {
        public void testMethodHeaderOnly(@Optional String foo, @JmsProperty double bar);
    }

    @Mock
    TestInterfaceHeaderOnlyPrimitiveDouble implPrimitiveDouble;

    @Test
    public void shouldSetHeaderOnlyPrimitiveDoublePropertyAnnotation() throws Exception {
        mockMessage();
        when(message.getDoubleProperty("arg1")).thenReturn(12.3);

        MessageDecoder<TestInterfaceHeaderOnlyPrimitiveDouble> decoder = MessageDecoder.of(
                TestInterfaceHeaderOnlyPrimitiveDouble.class, implPrimitiveDouble);

        decoder.onMessage(message);

        verify(implPrimitiveDouble).testMethodHeaderOnly("foo", 12.3);
    }

    public interface TestInterfaceHeaderOnlyList {
        public void testMethodHeaderOnly(@Optional String foo, @JmsProperty List<String> bar);
    }

    @Mock
    TestInterfaceHeaderOnlyList implList;

    @Test
    public void shouldSetHeaderOnlyListPropertyAnnotation() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
                + "<testMethodHeaderOnly>\n" //
                + "    <arg0>foo</arg0>\n" //
                + "</testMethodHeaderOnly>\n";
        when(message.getText()).thenReturn(xml);
        when(message.getPropertyNames()).thenReturn(new StringTokenizer("arg1[0] arg1[1] arg1[2]"));
        when(message.getStringProperty("arg1[0]")).thenReturn("one");
        when(message.getStringProperty("arg1[1]")).thenReturn("two");
        when(message.getStringProperty("arg1[2]")).thenReturn("three");

        MessageDecoder<TestInterfaceHeaderOnlyList> decoder = MessageDecoder.of(
                TestInterfaceHeaderOnlyList.class, implList);

        decoder.onMessage(message);

        verify(implList).testMethodHeaderOnly("foo", ImmutableList.of("one", "two", "three"));
    }

    public interface TestInterfaceHeaderOnlyArray {
        public void testMethodHeaderOnly(@Optional String foo, @JmsProperty String[] bar);
    }

    @Mock
    TestInterfaceHeaderOnlyArray implArray;

    @Test
    public void shouldSetHeaderOnlyArrayPropertyAnnotation() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
                + "<testMethodHeaderOnly>\n" //
                + "    <arg0>foo</arg0>\n" //
                + "</testMethodHeaderOnly>\n";
        when(message.getText()).thenReturn(xml);
        when(message.getPropertyNames()).thenReturn(new StringTokenizer("arg1[0] arg1[1] arg1[2]"));
        when(message.getStringProperty("arg1[0]")).thenReturn("one");
        when(message.getStringProperty("arg1[1]")).thenReturn("two");
        when(message.getStringProperty("arg1[2]")).thenReturn("three");

        MessageDecoder<TestInterfaceHeaderOnlyArray> decoder = MessageDecoder.of(
                TestInterfaceHeaderOnlyArray.class, implArray);

        decoder.onMessage(message);

        verify(implArray).testMethodHeaderOnly("foo", new String[] { "one", "two", "three" });
    }

    public interface TestInterfaceHeaderOnlySet {
        public void testMethodHeaderOnly(@Optional String foo, @JmsProperty Set<String> bar);
    }

    @Mock
    TestInterfaceHeaderOnlySet implSet;

    @Test
    public void shouldSetHeaderOnlySetPropertyAnnotation() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
                + "<testMethodHeaderOnly>\n" //
                + "    <arg0>foo</arg0>\n" //
                + "</testMethodHeaderOnly>\n";
        when(message.getText()).thenReturn(xml);
        when(message.getPropertyNames()).thenReturn(new StringTokenizer("arg1[0] arg1[1] arg1[2]"));
        when(message.getStringProperty("arg1[0]")).thenReturn("one");
        when(message.getStringProperty("arg1[1]")).thenReturn("two");
        when(message.getStringProperty("arg1[2]")).thenReturn("three");

        MessageDecoder<TestInterfaceHeaderOnlySet> decoder = MessageDecoder.of(
                TestInterfaceHeaderOnlySet.class, implSet);

        decoder.onMessage(message);

        verify(implSet).testMethodHeaderOnly("foo", ImmutableSet.of("one", "two", "three"));
    }

    public interface TestInterfaceHeaderOnlyMap {
        public void testMethodHeaderOnly(@Optional String foo, @JmsProperty Map<String, String> bar);
    }

    @Mock
    TestInterfaceHeaderOnlyMap implMap;

    @Test
    public void shouldSetHeaderOnlyMapPropertyAnnotation() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
                + "<testMethodHeaderOnly>\n" //
                + "    <arg0>foo</arg0>\n" //
                + "</testMethodHeaderOnly>\n";
        when(message.getText()).thenReturn(xml);
        when(message.getPropertyNames()).thenReturn(new StringTokenizer("arg1[A] arg1[B] arg1[C]"));
        when(message.getStringProperty("arg1[A]")).thenReturn("one");
        when(message.getStringProperty("arg1[B]")).thenReturn("two");
        when(message.getStringProperty("arg1[C]")).thenReturn("three");

        MessageDecoder<TestInterfaceHeaderOnlyMap> decoder = MessageDecoder.of(
                TestInterfaceHeaderOnlyMap.class, implMap);

        decoder.onMessage(message);

        verify(implMap).testMethodHeaderOnly("foo",
                ImmutableMap.of("A", "one", "B", "two", "C", "three"));
    }
}
