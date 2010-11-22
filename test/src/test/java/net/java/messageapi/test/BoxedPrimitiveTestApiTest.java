package net.java.messageapi.test;

import static net.java.messageapi.test.RegexMatcher.*;
import static org.junit.Assert.*;

import java.io.StringWriter;

import net.java.messageapi.adapter.xml.JaxbProvider;
import net.java.messageapi.adapter.xml.ToXmlEncoder;
import net.sf.twip.TwiP;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(TwiP.class)
public class BoxedPrimitiveTestApiTest {

    private static final String NS = "http://messageapi.java.net";

    private final BoxedPrimitivesTestApi testApi;

    private final StringWriter writer = new StringWriter();

    public BoxedPrimitiveTestApiTest(JaxbProvider jaxbProvider) {
        this.testApi = ToXmlEncoder.create(BoxedPrimitivesTestApi.class, writer, jaxbProvider);
    }

    private String getLine(StringWriter writer, int lineNumber) {
        return writer.toString().split("\n")[lineNumber];
    }

    private void matchFrame(String methodName, int lines) {
        assertThat(getLine(writer, 0),
                matches("<\\?xml version=\"1.0\" encoding=\"UTF-8\"( standalone=\"yes\")?\\?>"));
        assertThat(getLine(writer, 1), //
                matches("<(ns2:)?" + methodName + "( xmlns:ns2=\"" + NS + "\")?>"));
        assertThat(getLine(writer, lines), //
                matches("</(ns2:)?" + methodName + ">"));
    }

    private void matchElement(String name, String value, int line) {
        assertThat(getLine(writer, line), matches("\\s*<" + name + ">" + value + "</" + name + ">"));
    }

    @Test
    public void testBooleanCall() throws Exception {
        testApi.boxedBooleanCall(true);

        matchFrame("boxedBooleanCall", 3);
        matchElement("b", "true", 2);
    }

    @Test
    public void testByteCall() throws Exception {
        testApi.boxedByteCall(Byte.MAX_VALUE);

        matchFrame("boxedByteCall", 3);
        matchElement("b", "127", 2);
    }

    @Test
    public void testCharCall() throws Exception {
        testApi.boxedCharCall('a');

        matchFrame("boxedCharCall", 3);
        assertThat(getLine(writer, 2), matches("\\s*<" + "c" + ">" + "(97|a)" + "</" + "c" + ">"));
    }

    @Test
    public void testShortCall() throws Exception {
        testApi.boxedShortCall((short) 123);

        matchFrame("boxedShortCall", 3);
        matchElement("s", "123", 2);
    }

    @Test
    public void testIntCall() throws Exception {
        testApi.boxedIntCall(123);

        matchFrame("boxedIntCall", 3);
        matchElement("i", "123", 2);
    }

    @Test
    public void testLongCall() throws Exception {
        testApi.boxedLongCall(123L);

        matchFrame("boxedLongCall", 3);
        matchElement("l", "123", 2);
    }

    @Test
    public void testFloatCall() throws Exception {
        testApi.boxedFloatCall(12.3f);

        matchFrame("boxedFloatCall", 3);
        matchElement("f", "12.3", 2);
    }

    @Test
    public void testDoubleCall() throws Exception {
        testApi.boxedDoubleCall(123d);

        matchFrame("boxedDoubleCall", 3);
        matchElement("d", "123.0", 2);
    }
}
