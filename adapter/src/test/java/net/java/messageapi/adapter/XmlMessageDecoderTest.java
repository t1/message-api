package net.java.messageapi.adapter;

import static org.mockito.Mockito.*;

import java.util.*;

import javax.jms.*;

import net.java.messageapi.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * This is actually an integration test within the adapter classes
 */
@RunWith(MockitoJUnitRunner.class)
public class XmlMessageDecoderTest {

    @Mock
    private TextMessage message;

    protected void mockMessage() throws JMSException {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" //
                + "<testMethodHeaderOnly>\n" //
                + "    <arg0>foo</arg0>\n" //
                + "</testMethodHeaderOnly>\n";
        when(message.getText()).thenReturn(xml);
        when(message.getPropertyNames()).thenReturn(new StringTokenizer("arg1"));
    }

    public interface TestInterfaceHeaderOnlyString {
        public void testMethodHeaderOnly(@JmsOptional String foo, @JmsProperty String bar);
    }

    @Mock
    TestInterfaceHeaderOnlyString implString;

    @Test
    public void shouldSetHeaderOnlyStringPropertyAnnotation() throws Exception {
        mockMessage();
        when(message.getStringProperty("arg1")).thenReturn("123");

        XmlMessageDecoder<TestInterfaceHeaderOnlyString> decoder = XmlMessageDecoder.of(
                TestInterfaceHeaderOnlyString.class, implString);

        decoder.onMessage(message);

        verify(implString).testMethodHeaderOnly("foo", "123");
    }

    public interface TestInterfaceHeaderOnlyBoolean {
        public void testMethodHeaderOnly(@JmsOptional String foo, @JmsProperty Boolean bar);
    }

    @Mock
    TestInterfaceHeaderOnlyBoolean implBoolean;

    @Test
    public void shouldSetHeaderOnlyBooleanPropertyAnnotation() throws Exception {
        mockMessage();
        when(message.getBooleanProperty("arg1")).thenReturn(true);

        XmlMessageDecoder<TestInterfaceHeaderOnlyBoolean> decoder = XmlMessageDecoder.of(
                TestInterfaceHeaderOnlyBoolean.class, implBoolean);

        decoder.onMessage(message);

        verify(implBoolean).testMethodHeaderOnly("foo", true);
    }

    public interface TestInterfaceHeaderOnlyPrimitiveBoolean {
        public void testMethodHeaderOnly(@JmsOptional String foo, @JmsProperty boolean bar);
    }

    @Mock
    TestInterfaceHeaderOnlyPrimitiveBoolean implPrimitiveBoolean;

    @Test
    public void shouldSetHeaderOnlyPrimitiveBooleanPropertyAnnotation() throws Exception {
        mockMessage();
        when(message.getBooleanProperty("arg1")).thenReturn(true);

        XmlMessageDecoder<TestInterfaceHeaderOnlyPrimitiveBoolean> decoder = XmlMessageDecoder.of(
                TestInterfaceHeaderOnlyPrimitiveBoolean.class, implPrimitiveBoolean);

        decoder.onMessage(message);

        verify(implPrimitiveBoolean).testMethodHeaderOnly("foo", true);
    }

    public interface TestInterfaceHeaderOnlyByte {
        public void testMethodHeaderOnly(@JmsOptional String foo, @JmsProperty Byte bar);
    }

    @Mock
    TestInterfaceHeaderOnlyByte implByte;

    @Test
    public void shouldSetHeaderOnlyBytePropertyAnnotation() throws Exception {
        mockMessage();
        when(message.getByteProperty("arg1")).thenReturn((byte) 123);

        XmlMessageDecoder<TestInterfaceHeaderOnlyByte> decoder = XmlMessageDecoder.of(
                TestInterfaceHeaderOnlyByte.class, implByte);

        decoder.onMessage(message);

        verify(implByte).testMethodHeaderOnly("foo", (byte) 123);
    }

    public interface TestInterfaceHeaderOnlyPrimitiveByte {
        public void testMethodHeaderOnly(@JmsOptional String foo, @JmsProperty byte bar);
    }

    @Mock
    TestInterfaceHeaderOnlyPrimitiveByte implPrimitiveByte;

    @Test
    public void shouldSetHeaderOnlyPrimitiveBytePropertyAnnotation() throws Exception {
        mockMessage();
        when(message.getByteProperty("arg1")).thenReturn((byte) 123);

        XmlMessageDecoder<TestInterfaceHeaderOnlyPrimitiveByte> decoder = XmlMessageDecoder.of(
                TestInterfaceHeaderOnlyPrimitiveByte.class, implPrimitiveByte);

        decoder.onMessage(message);

        verify(implPrimitiveByte).testMethodHeaderOnly("foo", (byte) 123);
    }

    public interface TestInterfaceHeaderOnlyShort {
        public void testMethodHeaderOnly(@JmsOptional String foo, @JmsProperty Short bar);
    }

    @Mock
    TestInterfaceHeaderOnlyShort implShort;

    @Test
    public void shouldSetHeaderOnlyShortPropertyAnnotation() throws Exception {
        mockMessage();
        when(message.getShortProperty("arg1")).thenReturn((short) 123);

        XmlMessageDecoder<TestInterfaceHeaderOnlyShort> decoder = XmlMessageDecoder.of(
                TestInterfaceHeaderOnlyShort.class, implShort);

        decoder.onMessage(message);

        verify(implShort).testMethodHeaderOnly("foo", (short) 123);
    }

    public interface TestInterfaceHeaderOnlyPrimitiveShort {
        public void testMethodHeaderOnly(@JmsOptional String foo, @JmsProperty short bar);
    }

    @Mock
    TestInterfaceHeaderOnlyPrimitiveShort implPrimitiveShort;

    @Test
    public void shouldSetHeaderOnlyPrimitiveShortPropertyAnnotation() throws Exception {
        mockMessage();
        when(message.getShortProperty("arg1")).thenReturn((short) 123);

        XmlMessageDecoder<TestInterfaceHeaderOnlyPrimitiveShort> decoder = XmlMessageDecoder.of(
                TestInterfaceHeaderOnlyPrimitiveShort.class, implPrimitiveShort);

        decoder.onMessage(message);

        verify(implPrimitiveShort).testMethodHeaderOnly("foo", (short) 123);
    }

    public interface TestInterfaceHeaderOnlyInteger {
        public void testMethodHeaderOnly(@JmsOptional String foo, @JmsProperty Integer bar);
    }

    @Mock
    TestInterfaceHeaderOnlyInteger implInteger;

    @Test
    public void shouldSetHeaderOnlyIntegerPropertyAnnotation() throws Exception {
        mockMessage();
        when(message.getIntProperty("arg1")).thenReturn(123);

        XmlMessageDecoder<TestInterfaceHeaderOnlyInteger> decoder = XmlMessageDecoder.of(
                TestInterfaceHeaderOnlyInteger.class, implInteger);

        decoder.onMessage(message);

        verify(implInteger).testMethodHeaderOnly("foo", 123);
    }

    public interface TestInterfaceHeaderOnlyInt {
        public void testMethodHeaderOnly(@JmsOptional String foo, @JmsProperty int bar);
    }

    @Mock
    TestInterfaceHeaderOnlyInt implInt;

    @Test
    public void shouldSetHeaderOnlyIntPropertyAnnotation() throws Exception {
        mockMessage();
        when(message.getIntProperty("arg1")).thenReturn(123);

        XmlMessageDecoder<TestInterfaceHeaderOnlyInt> decoder = XmlMessageDecoder.of(TestInterfaceHeaderOnlyInt.class,
                implInt);

        decoder.onMessage(message);

        verify(implInt).testMethodHeaderOnly("foo", 123);
    }

    public interface TestInterfaceHeaderOnlyLong {
        public void testMethodHeaderOnly(@JmsOptional String foo, @JmsProperty Long bar);
    }

    @Mock
    TestInterfaceHeaderOnlyLong implLong;

    @Test
    public void shouldSetHeaderOnlyLongPropertyAnnotation() throws Exception {
        mockMessage();
        when(message.getLongProperty("arg1")).thenReturn(123L);

        XmlMessageDecoder<TestInterfaceHeaderOnlyLong> decoder = XmlMessageDecoder.of(
                TestInterfaceHeaderOnlyLong.class, implLong);

        decoder.onMessage(message);

        verify(implLong).testMethodHeaderOnly("foo", 123L);
    }

    public interface TestInterfaceHeaderOnlyPrimitiveLong {
        public void testMethodHeaderOnly(@JmsOptional String foo, @JmsProperty long bar);
    }

    @Mock
    TestInterfaceHeaderOnlyPrimitiveLong implPrimitiveLong;

    @Test
    public void shouldSetHeaderOnlyPrimitiveLongPropertyAnnotation() throws Exception {
        mockMessage();
        when(message.getLongProperty("arg1")).thenReturn(123L);

        XmlMessageDecoder<TestInterfaceHeaderOnlyPrimitiveLong> decoder = XmlMessageDecoder.of(
                TestInterfaceHeaderOnlyPrimitiveLong.class, implPrimitiveLong);

        decoder.onMessage(message);

        verify(implPrimitiveLong).testMethodHeaderOnly("foo", 123L);
    }

    public interface TestInterfaceHeaderOnlyFloat {
        public void testMethodHeaderOnly(@JmsOptional String foo, @JmsProperty Float bar);
    }

    @Mock
    TestInterfaceHeaderOnlyFloat implFloat;

    @Test
    public void shouldSetHeaderOnlyFloatPropertyAnnotation() throws Exception {
        mockMessage();
        when(message.getFloatProperty("arg1")).thenReturn(12.3f);

        XmlMessageDecoder<TestInterfaceHeaderOnlyFloat> decoder = XmlMessageDecoder.of(
                TestInterfaceHeaderOnlyFloat.class, implFloat);

        decoder.onMessage(message);

        verify(implFloat).testMethodHeaderOnly("foo", 12.3f);
    }

    public interface TestInterfaceHeaderOnlyPrimitiveFloat {
        public void testMethodHeaderOnly(@JmsOptional String foo, @JmsProperty float bar);
    }

    @Mock
    TestInterfaceHeaderOnlyPrimitiveFloat implPrimitiveFloat;

    @Test
    public void shouldSetHeaderOnlyPrimitiveFloatPropertyAnnotation() throws Exception {
        mockMessage();
        when(message.getFloatProperty("arg1")).thenReturn(12.3f);

        XmlMessageDecoder<TestInterfaceHeaderOnlyPrimitiveFloat> decoder = XmlMessageDecoder.of(
                TestInterfaceHeaderOnlyPrimitiveFloat.class, implPrimitiveFloat);

        decoder.onMessage(message);

        verify(implPrimitiveFloat).testMethodHeaderOnly("foo", 12.3f);
    }

    public interface TestInterfaceHeaderOnlyDouble {
        public void testMethodHeaderOnly(@JmsOptional String foo, @JmsProperty Double bar);
    }

    @Mock
    TestInterfaceHeaderOnlyDouble implDouble;

    @Test
    public void shouldSetHeaderOnlyDoublePropertyAnnotation() throws Exception {
        mockMessage();
        when(message.getDoubleProperty("arg1")).thenReturn(12.3);

        XmlMessageDecoder<TestInterfaceHeaderOnlyDouble> decoder = XmlMessageDecoder.of(
                TestInterfaceHeaderOnlyDouble.class, implDouble);

        decoder.onMessage(message);

        verify(implDouble).testMethodHeaderOnly("foo", 12.3);
    }

    public interface TestInterfaceHeaderOnlyPrimitiveDouble {
        public void testMethodHeaderOnly(@JmsOptional String foo, @JmsProperty double bar);
    }

    @Mock
    TestInterfaceHeaderOnlyPrimitiveDouble implPrimitiveDouble;

    @Test
    public void shouldSetHeaderOnlyPrimitiveDoublePropertyAnnotation() throws Exception {
        mockMessage();
        when(message.getDoubleProperty("arg1")).thenReturn(12.3);

        XmlMessageDecoder<TestInterfaceHeaderOnlyPrimitiveDouble> decoder = XmlMessageDecoder.of(
                TestInterfaceHeaderOnlyPrimitiveDouble.class, implPrimitiveDouble);

        decoder.onMessage(message);

        verify(implPrimitiveDouble).testMethodHeaderOnly("foo", 12.3);
    }

    public interface TestInterfaceHeaderOnlyList {
        public void testMethodHeaderOnly(@JmsOptional String foo, @JmsProperty List<String> bar);
    }

    @Mock
    TestInterfaceHeaderOnlyList implList;

    @Test
    public void shouldSetHeaderOnlyListPropertyAnnotation() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" + "<testMethodHeaderOnly>\n" //
                + "    <arg0>foo</arg0>\n" //
                + "</testMethodHeaderOnly>\n";
        when(message.getText()).thenReturn(xml);
        when(message.getPropertyNames()).thenReturn(new StringTokenizer("arg1[0] arg1[1] arg1[2]"));
        when(message.getStringProperty("arg1[0]")).thenReturn("one");
        when(message.getStringProperty("arg1[1]")).thenReturn("two");
        when(message.getStringProperty("arg1[2]")).thenReturn("three");

        XmlMessageDecoder<TestInterfaceHeaderOnlyList> decoder = XmlMessageDecoder.of(
                TestInterfaceHeaderOnlyList.class, implList);

        decoder.onMessage(message);

        verify(implList).testMethodHeaderOnly("foo", Arrays.asList("one", "two", "three"));
    }

    public interface TestInterfaceHeaderOnlyArray {
        public void testMethodHeaderOnly(@JmsOptional String foo, @JmsProperty String[] bar);
    }

    @Mock
    TestInterfaceHeaderOnlyArray implArray;

    @Test
    public void shouldSetHeaderOnlyArrayPropertyAnnotation() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" + "<testMethodHeaderOnly>\n" //
                + "    <arg0>foo</arg0>\n" //
                + "</testMethodHeaderOnly>\n";
        when(message.getText()).thenReturn(xml);
        when(message.getPropertyNames()).thenReturn(new StringTokenizer("arg1[0] arg1[1] arg1[2]"));
        when(message.getStringProperty("arg1[0]")).thenReturn("one");
        when(message.getStringProperty("arg1[1]")).thenReturn("two");
        when(message.getStringProperty("arg1[2]")).thenReturn("three");

        XmlMessageDecoder<TestInterfaceHeaderOnlyArray> decoder = XmlMessageDecoder.of(
                TestInterfaceHeaderOnlyArray.class, implArray);

        decoder.onMessage(message);

        verify(implArray).testMethodHeaderOnly("foo", new String[] { "one", "two", "three" });
    }

    public interface TestInterfaceHeaderOnlySet {
        public void testMethodHeaderOnly(@JmsOptional String foo, @JmsProperty Set<String> bar);
    }

    @Mock
    TestInterfaceHeaderOnlySet implSet;

    @Test
    public void shouldSetHeaderOnlySetPropertyAnnotation() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" + "<testMethodHeaderOnly>\n" //
                + "    <arg0>foo</arg0>\n" //
                + "</testMethodHeaderOnly>\n";
        when(message.getText()).thenReturn(xml);
        when(message.getPropertyNames()).thenReturn(new StringTokenizer("arg1[0] arg1[1] arg1[2]"));
        when(message.getStringProperty("arg1[0]")).thenReturn("one");
        when(message.getStringProperty("arg1[1]")).thenReturn("two");
        when(message.getStringProperty("arg1[2]")).thenReturn("three");

        XmlMessageDecoder<TestInterfaceHeaderOnlySet> decoder = XmlMessageDecoder.of(TestInterfaceHeaderOnlySet.class,
                implSet);

        decoder.onMessage(message);

        Set<String> set = new HashSet<String>(Arrays.asList("one", "two", "three"));
        verify(implSet).testMethodHeaderOnly("foo", set);
    }

    public interface TestInterfaceHeaderOnlyMap {
        public void testMethodHeaderOnly(@JmsOptional String foo, @JmsProperty Map<String, String> bar);
    }

    @Mock
    TestInterfaceHeaderOnlyMap implMap;

    @Test
    public void shouldSetHeaderOnlyMapPropertyAnnotation() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" + "<testMethodHeaderOnly>\n" //
                + "    <arg0>foo</arg0>\n" //
                + "</testMethodHeaderOnly>\n";
        when(message.getText()).thenReturn(xml);
        when(message.getPropertyNames()).thenReturn(new StringTokenizer("arg1[A] arg1[B] arg1[C]"));
        when(message.getStringProperty("arg1[A]")).thenReturn("one");
        when(message.getStringProperty("arg1[B]")).thenReturn("two");
        when(message.getStringProperty("arg1[C]")).thenReturn("three");

        XmlMessageDecoder<TestInterfaceHeaderOnlyMap> decoder = XmlMessageDecoder.of(TestInterfaceHeaderOnlyMap.class,
                implMap);

        decoder.onMessage(message);

        Map<String, String> map = new HashMap<String, String>();
        map.put("A", "one");
        map.put("B", "two");
        map.put("C", "three");
        verify(implMap).testMethodHeaderOnly("foo", map);
    }
}
