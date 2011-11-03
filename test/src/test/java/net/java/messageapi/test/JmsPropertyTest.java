package net.java.messageapi.test;

import static org.junit.Assert.*;

import java.util.*;

import javax.jms.TextMessage;

import net.java.messageapi.adapter.MessageSender;

import org.junit.Test;

public class JmsPropertyTest extends AbstractJmsSenderFactoryTest {
    @Override
    public TextMessage captureMessage() {
        return (TextMessage) super.captureMessage();
    }

    @Test
    public void shouldNotIncludePropertiesInPayload() throws Exception {
        JmsPropertyApi api = MessageSender.of(JmsPropertyApi.class);

        api.jmsPropertyMethod("ooo", "ttt");

        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" //
                + "<jmsPropertyMethod>\n" //
                + "    <two>ttt</two>\n" //
                + "</jmsPropertyMethod>\n" //
        , captureMessage().getText());
    }

    @Test
    public void shouldIncludePropertiesInHeader() throws Exception {
        JmsPropertyApi api = MessageSender.of(JmsPropertyApi.class);

        api.jmsPropertyMethod("ooo", "ttt");

        assertEquals("ooo", captureMessage().getStringProperty("one"));
    }

    @Test
    public void shouldIncludeBoolean() throws Exception {
        JmsPropertyApi api = MessageSender.of(JmsPropertyApi.class);

        api.jmsPropertyBooleanMethod(true, "ttt");

        assertEquals(true, captureMessage().getBooleanProperty("one"));
    }

    @Test
    public void shouldIncludePrimitiveBoolean() throws Exception {
        JmsPropertyApi api = MessageSender.of(JmsPropertyApi.class);

        api.jmsPropertyPrimitiveBooleanMethod(true, "ttt");

        assertEquals(true, captureMessage().getBooleanProperty("one"));
    }

    @Test
    public void shouldIncludeByte() throws Exception {
        JmsPropertyApi api = MessageSender.of(JmsPropertyApi.class);

        api.jmsPropertyByteMethod((byte) 123, "ttt");

        assertEquals(123, captureMessage().getByteProperty("one"));
    }

    @Test
    public void shouldIncludePrimitiveByte() throws Exception {
        JmsPropertyApi api = MessageSender.of(JmsPropertyApi.class);

        api.jmsPropertyPrimitiveByteMethod((byte) 123, "ttt");

        assertEquals(123, captureMessage().getByteProperty("one"));
    }

    @Test
    public void shouldIncludeShort() throws Exception {
        JmsPropertyApi api = MessageSender.of(JmsPropertyApi.class);

        api.jmsPropertyShortMethod((short) 1234, "ttt");

        assertEquals(1234, captureMessage().getShortProperty("one"));
    }

    @Test
    public void shouldIncludePrimitiveShort() throws Exception {
        JmsPropertyApi api = MessageSender.of(JmsPropertyApi.class);

        api.jmsPropertyPrimitiveShortMethod((short) 1234, "ttt");

        assertEquals(1234, captureMessage().getShortProperty("one"));
    }

    @Test
    public void shouldIncludeChar() throws Exception {
        JmsPropertyApi api = MessageSender.of(JmsPropertyApi.class);

        api.jmsPropertyCharMethod('c', "ttt");

        assertEquals("c", captureMessage().getStringProperty("one"));
    }

    @Test
    public void shouldIncludePrimitiveChar() throws Exception {
        JmsPropertyApi api = MessageSender.of(JmsPropertyApi.class);

        api.jmsPropertyPrimitiveCharMethod('c', "ttt");

        assertEquals("c", captureMessage().getStringProperty("one"));
    }

    @Test
    public void shouldIncludeFloat() throws Exception {
        JmsPropertyApi api = MessageSender.of(JmsPropertyApi.class);

        api.jmsPropertyFloatMethod(123.4f, "ttt");

        assertEquals((Float) 123.4f, (Float) captureMessage().getFloatProperty("one"));
    }

    @Test
    public void shouldIncludePrimitiveFloat() throws Exception {
        JmsPropertyApi api = MessageSender.of(JmsPropertyApi.class);

        api.jmsPropertyPrimitiveFloatMethod(123.4f, "ttt");

        assertEquals((Float) 123.4f, (Float) captureMessage().getFloatProperty("one"));
    }

    @Test
    public void shouldIncludeDouble() throws Exception {
        JmsPropertyApi api = MessageSender.of(JmsPropertyApi.class);

        api.jmsPropertyDoubleMethod(123.456d, "ttt");

        assertEquals((Double) 123.456, (Double) captureMessage().getDoubleProperty("one"));
    }

    @Test
    public void shouldIncludePrimitiveDouble() throws Exception {
        JmsPropertyApi api = MessageSender.of(JmsPropertyApi.class);

        api.jmsPropertyPrimitiveDoubleMethod(123.456d, "ttt");

        assertEquals((Double) 123.456, (Double) captureMessage().getDoubleProperty("one"));
    }

    @Test
    public void shouldIncludeInt() throws Exception {
        JmsPropertyApi api = MessageSender.of(JmsPropertyApi.class);

        api.jmsPropertyIntMethod(123, "ttt");

        assertEquals(123, captureMessage().getIntProperty("one"));
    }

    @Test
    public void shouldIncludePrimitiveInt() throws Exception {
        JmsPropertyApi api = MessageSender.of(JmsPropertyApi.class);

        api.jmsPropertyPrimitiveIntMethod(123, "ttt");

        assertEquals(123, captureMessage().getIntProperty("one"));
    }

    @Test
    public void shouldIncludeLong() throws Exception {
        JmsPropertyApi api = MessageSender.of(JmsPropertyApi.class);

        api.jmsPropertyLongMethod(123L, "ttt");

        assertEquals(123L, captureMessage().getLongProperty("one"));
    }

    @Test
    public void shouldIncludePrimitiveLong() throws Exception {
        JmsPropertyApi api = MessageSender.of(JmsPropertyApi.class);

        api.jmsPropertyPrimitiveLongMethod(123L, "ttt");

        assertEquals(123L, captureMessage().getLongProperty("one"));
    }

    @Test
    public void shouldNotIncludeUnannotatedParameterInHeader() throws Exception {
        JmsPropertyApi api = MessageSender.of(JmsPropertyApi.class);

        api.jmsPropertyMethod("ooo", "ttt");

        assertNull(captureMessage().getStringProperty("two"));
    }

    @Test
    public void shouldIncludeTwo() throws Exception {
        JmsPropertyApi api = MessageSender.of(JmsPropertyApi.class);

        api.jmsPropertyTwiceMethod("ooo", "ttt");

        assertEquals("ooo", captureMessage().getStringProperty("one"));
        assertEquals("ttt", captureMessage().getStringProperty("two"));
    }

    @Test
    public void shouldNotIncludeHeaderOnlyPropertiesInPayload() throws Exception {
        JmsPropertyApi api = MessageSender.of(JmsPropertyApi.class);

        api.jmsPropertyMethodWithHeaderOnly("ooo", "ttt");

        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" //
                + "<jmsPropertyMethodWithHeaderOnly>\n" //
                + "    <two>ttt</two>\n" //
                + "</jmsPropertyMethodWithHeaderOnly>\n" //
        , captureMessage().getText());
    }

    @Test
    public void shouldIncludeHeaderOnly() throws Exception {
        JmsPropertyApi api = MessageSender.of(JmsPropertyApi.class);

        api.jmsPropertyMethodWithHeaderOnly("ooo", "ttt");

        assertEquals("ooo", captureMessage().getStringProperty("one"));
    }

    @Test
    public void shouldNotIncludeNestedPropertiesInPayload() throws Exception {
        JmsPropertyApi api = MessageSender.of(JmsPropertyApi.class);

        JmsPropertyApi.NestedAnnotated one = new JmsPropertyApi.NestedAnnotated();
        one.nested = "ooo";
        api.jmsPropertyInNestedClass(one, "ttt");

        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" //
                + "<jmsPropertyInNestedClass>\n" //
                + "    <two>ttt</two>\n" //
                + "</jmsPropertyInNestedClass>\n" //
        , captureMessage().getText());
    }

    @Test
    public void shouldIncludeNested() throws Exception {
        JmsPropertyApi api = MessageSender.of(JmsPropertyApi.class);

        JmsPropertyApi.NestedAnnotated one = new JmsPropertyApi.NestedAnnotated();
        one.nested = "ooo";
        api.jmsPropertyInNestedClass(one, "ttt");

        assertEquals("ooo", captureMessage().getStringProperty("one/nested"));
    }

    @Test
    public void shouldNotIncludeNestedStatic() throws Exception {
        JmsPropertyApi api = MessageSender.of(JmsPropertyApi.class);

        JmsPropertyApi.NestedAnnotated one = new JmsPropertyApi.NestedAnnotated();
        one.nested = "ooo";
        api.jmsPropertyInNestedClass(one, "ttt");

        assertNull(captureMessage().getStringProperty("one/serialVersionUID"));
    }

    @Test
    public void shouldNotIncludeNestedNull() throws Exception {
        JmsPropertyApi api = MessageSender.of(JmsPropertyApi.class);

        JmsPropertyApi.NestedAnnotated one = new JmsPropertyApi.NestedAnnotated();
        one.nested = null;
        api.jmsPropertyInNestedClass(one, "ttt");

        assertNull(captureMessage().getStringProperty("one/nested"));
    }

    @Test
    public void shouldNotIncludeNull() throws Exception {
        JmsPropertyApi api = MessageSender.of(JmsPropertyApi.class);

        api.jmsPropertyMethod(null, null);

        assertNull(captureMessage().getStringProperty("one"));
        assertNull(captureMessage().getStringProperty("two"));
    }

    @Test
    public void shouldNotCycle() throws Exception {
        JmsPropertyApi api = MessageSender.of(JmsPropertyApi.class);

        JmsPropertyApi.Cyclic one = new JmsPropertyApi.Cyclic();
        one.cycle = one;
        api.jmsPropertyInCyclicClass(one, "ttt");

        assertNull(captureMessage().getStringProperty("one/nested"));
    }

    @Test
    public void shouldFoldListValue() throws Exception {
        JmsPropertyApi api = MessageSender.of(JmsPropertyApi.class);

        List<String> list = new ArrayList<String>();
        list.add("one");
        list.add("two");
        api.jmsPropertyOnCollectionType(list);

        assertEquals("one", captureMessage().getStringProperty("param[0]"));
        assertEquals("two", captureMessage().getStringProperty("param[1]"));
    }

    @Test
    public void shouldFoldSetValue() throws Exception {
        JmsPropertyApi api = MessageSender.of(JmsPropertyApi.class);

        Set<String> set = new TreeSet<String>();
        set.add("one");
        set.add("two");
        api.jmsPropertyOnCollectionType(set);

        assertEquals("one", captureMessage().getStringProperty("param[0]"));
        assertEquals("two", captureMessage().getStringProperty("param[1]"));
    }

    @Test
    public void shouldFoldArrayValue() throws Exception {
        JmsPropertyApi api = MessageSender.of(JmsPropertyApi.class);

        String[] array = { "one", "two" };
        api.jmsPropertyOnArrayType(array);

        assertEquals("one", captureMessage().getStringProperty("param[0]"));
        assertEquals("two", captureMessage().getStringProperty("param[1]"));
    }

    @Test
    public void shouldFoldCyclicValue() throws Exception {
        // TODO test with a hand-made cycle; collections are sorted out before
        JmsPropertyApi api = MessageSender.of(JmsPropertyApi.class);

        LinkedList<String> list = new LinkedList<String>(); // has cycles!
        list.add("one");
        list.add("two");
        api.jmsPropertyOnCollectionType(list);

        assertEquals("one", captureMessage().getStringProperty("param[0]"));
        assertEquals("two", captureMessage().getStringProperty("param[1]"));
    }
}
