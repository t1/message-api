package net.java.messageapi.test;

import static net.java.messageapi.test.RegexMatcher.*;
import static org.junit.Assert.*;

import java.io.StringWriter;

import net.java.messageapi.adapter.xml.JaxbProvider;
import net.java.messageapi.adapter.xml.ToXmlSenderFactory;
import net.java.messageapi.test.PrimitivesTestApi;
import net.sf.twip.TwiP;

import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(TwiP.class)
public class PrimitiveTestApiTest {

    private static final String NS = "http://messageapi.java.net";

    private final StringWriter writer = new StringWriter();

    private final PrimitivesTestApi testApi;

    public PrimitiveTestApiTest(JaxbProvider jaxbProvider) {
        testApi = ToXmlSenderFactory.create(PrimitivesTestApi.class, jaxbProvider, writer).get();
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
        testApi.booleanCall(true);

        matchFrame("booleanCall", 3);
        matchElement("b", "true", 2);
    }

    @Test
    public void testByteCall() throws Exception {
        testApi.byteCall(Byte.MAX_VALUE);

        matchFrame("byteCall", 3);
        matchElement("b", "127", 2);
    }

    @Test
    public void testCharCall() throws Exception {
        testApi.charCall('a');

        matchFrame("charCall", 3);
        assertThat(getLine(writer, 2), matches("\\s*<" + "c" + ">" + "(97|a)" + "</" + "c" + ">"));
    }

    @Test
    public void testShortCall() throws Exception {
        testApi.shortCall((short) 123);

        matchFrame("shortCall", 3);
        matchElement("s", "123", 2);
    }

    @Test
    public void testIntCall() throws Exception {
        testApi.intCall(123);

        matchFrame("intCall", 3);
        matchElement("i", "123", 2);
    }

    @Test
    public void testLongCall() throws Exception {
        testApi.longCall(123L);

        matchFrame("longCall", 3);
        matchElement("l", "123", 2);
    }

    @Test
    public void testFloatCall() throws Exception {
        testApi.floatCall(12.3f);

        matchFrame("floatCall", 3);
        matchElement("f", "12.3", 2);
    }

    @Test
    public void testDoubleCall() throws Exception {
        testApi.doubleCall(123d);

        matchFrame("doubleCall", 3);
        matchElement("d", "123.0", 2);
    }

    @Test
    public void testTwoDoublesCall() throws Exception {
        testApi.twoDoublesCall(1.2d, 3.4d);

        matchFrame("twoDoublesCall", 4);
        matchElement("d", "1.2", 2);
        matchElement("e", "3.4", 3);
    }
}
