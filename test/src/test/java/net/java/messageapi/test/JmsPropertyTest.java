package net.java.messageapi.test;

import static org.junit.Assert.*;

import javax.jms.TextMessage;

import net.java.messageapi.adapter.MessageSender;

import org.junit.Test;

public class JmsPropertyTest extends AbstractJmsSenderFactoryTest {
    @Override
    public TextMessage captureMessage() {
        return (TextMessage) super.captureMessage();
    }

    @Test
    public void jmsPropertyMethodShouldIncludePropertiesInPayload() throws Exception {
        JmsPropertyApi api = MessageSender.of(JmsPropertyApi.class);

        api.jmsPropertyMethod("ooo", "ttt");

        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" //
                + "<jmsPropertyMethod>\n" //
                + "    <one>ooo</one>\n" //
                + "    <two>ttt</two>\n" //
                + "</jmsPropertyMethod>\n" //
        , captureMessage().getText());
    }

    @Test
    public void jmsPropertyMethodShouldIncludePropertiesInHeader() throws Exception {
        JmsPropertyApi api = MessageSender.of(JmsPropertyApi.class);

        api.jmsPropertyMethod("ooo", "ttt");

        assertEquals("ooo", captureMessage().getStringProperty("one"));
    }

    @Test
    public void jmsPropertyMethodShouldIncludeBooleanPropertiesInHeader() throws Exception {
        JmsPropertyApi api = MessageSender.of(JmsPropertyApi.class);

        api.jmsPropertyBooleanMethod(true, "ttt");

        assertEquals(true, captureMessage().getBooleanProperty("one"));
    }

    @Test
    public void jmsPropertyMethodShouldIncludePrimitiveBooleanPropertiesInHeader() throws Exception {
        JmsPropertyApi api = MessageSender.of(JmsPropertyApi.class);

        api.jmsPropertyPrimitiveBooleanMethod(true, "ttt");

        assertEquals(true, captureMessage().getBooleanProperty("one"));
    }

    @Test
    public void jmsPropertyMethodShouldIncludeIntPropertiesInHeader() throws Exception {
        JmsPropertyApi api = MessageSender.of(JmsPropertyApi.class);

        api.jmsPropertyIntMethod(123, "ttt");

        assertEquals(123, captureMessage().getIntProperty("one"));
    }

    @Test
    public void jmsPropertyMethodShouldIncludePrimitiveIntPropertiesInHeader() throws Exception {
        JmsPropertyApi api = MessageSender.of(JmsPropertyApi.class);

        api.jmsPropertyPrimitiveIntMethod(123, "ttt");

        assertEquals(123, captureMessage().getIntProperty("one"));
    }

    @Test
    public void jmsPropertyMethodShouldIncludeLongPropertiesInHeader() throws Exception {
        JmsPropertyApi api = MessageSender.of(JmsPropertyApi.class);

        api.jmsPropertyLongMethod(123L, "ttt");

        assertEquals(123L, captureMessage().getLongProperty("one"));
    }

    @Test
    public void jmsPropertyMethodShouldIncludePrimitiveLongPropertiesInHeader() throws Exception {
        JmsPropertyApi api = MessageSender.of(JmsPropertyApi.class);

        api.jmsPropertyPrimitiveLongMethod(123L, "ttt");

        assertEquals(123L, captureMessage().getLongProperty("one"));
    }

    @Test
    public void jmsPropertyMethodShouldNotIncludeUnannotatedParameterInHeader() throws Exception {
        JmsPropertyApi api = MessageSender.of(JmsPropertyApi.class);

        api.jmsPropertyMethod("ooo", "ttt");

        assertNull(captureMessage().getStringProperty("two"));
    }

    @Test
    public void jmsPropertyMethodShouldIncludeTwoPropertiesInHeader() throws Exception {
        JmsPropertyApi api = MessageSender.of(JmsPropertyApi.class);

        api.jmsPropertyTwiceMethod("ooo", "ttt");

        assertEquals("ooo", captureMessage().getStringProperty("one"));
        assertEquals("ttt", captureMessage().getStringProperty("two"));
    }

    @Test
    public void jmsPropertyMethodShouldNotIncludeHeaderOnlyPropertiesInPayload() throws Exception {
        JmsPropertyApi api = MessageSender.of(JmsPropertyApi.class);

        api.jmsPropertyMethodWithHeaderOnly("ooo", "ttt");

        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" //
                + "<jmsPropertyMethodWithHeaderOnly>\n" //
                + "    <two>ttt</two>\n" //
                + "</jmsPropertyMethodWithHeaderOnly>\n" //
        , captureMessage().getText());
    }

    @Test
    public void jmsPropertyMethodShouldIncludeHeaderOnlyPropertiesInHeader() throws Exception {
        JmsPropertyApi api = MessageSender.of(JmsPropertyApi.class);

        api.jmsPropertyMethodWithHeaderOnly("ooo", "ttt");

        assertEquals("ooo", captureMessage().getStringProperty("one"));
    }

    @Test
    public void jmsPropertyMethodShouldIncludeNestedPropertiesInPayload() throws Exception {
        JmsPropertyApi api = MessageSender.of(JmsPropertyApi.class);

        JmsPropertyApi.NestedAnnotated one = new JmsPropertyApi.NestedAnnotated();
        one.nested = "ooo";
        api.jmsPropertyInNestedClass(one, "ttt");

        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" //
                + "<jmsPropertyInNestedClass>\n" //
                + "    <one>\n" //
                + "        <nested>ooo</nested>\n" //
                + "    </one>\n" //
                + "    <two>ttt</two>\n" //
                + "</jmsPropertyInNestedClass>\n" //
        , captureMessage().getText());
    }

    @Test
    public void jmsPropertyMethodShouldIncludeNestedPropertiesInHeader() throws Exception {
        JmsPropertyApi api = MessageSender.of(JmsPropertyApi.class);

        JmsPropertyApi.NestedAnnotated one = new JmsPropertyApi.NestedAnnotated();
        one.nested = "ooo";
        api.jmsPropertyInNestedClass(one, "ttt");

        assertEquals("ooo", captureMessage().getStringProperty("nested"));
    }
}
