package net.java.messageapi.test;

import static net.java.messageapi.test.RegexMatcher.*;
import static org.junit.Assert.*;

import java.io.StringWriter;

import net.java.messageapi.adapter.MessageSenderRegistry;
import net.java.messageapi.adapter.xml.JaxbProvider;
import net.java.messageapi.adapter.xml.ToXmlSenderFactory;
import net.java.messageapi.test.TestApi;
import net.java.messageapi.test.TestType;
import net.sf.twip.TwiP;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(TwiP.class)
public class TestApiSendTest {

    private static final String NS = "http://messageapi.java.net";

    private final MessageSenderRegistry registry = new MessageSenderRegistry();
    private final StringWriter writer = new StringWriter();

    private final TestApi testApi;

    public TestApiSendTest(JaxbProvider jaxbProvider) {
        registry.add(TestApi.class, ToXmlSenderFactory.create(TestApi.class, jaxbProvider, writer));
        testApi = registry.get(TestApi.class);
    }

    private String getLine(StringWriter writer, int lineNumber) {
        return writer.toString().split("\n")[lineNumber];
    }

    private void matchFrame(String methodName, int lines) {
        assertThat(getLine(writer, 0),
                matches("<\\?xml version=\"1.0\" encoding=\"UTF-8\"( standalone=\"yes\")?\\?>"));
        String elementStart = "<(ns2:)?" + methodName + "( xmlns:ns2=\"" + NS + "\")?";
        if (lines == 2) {
            assertThat(getLine(writer, 1), matches(elementStart + "/>"));
        } else {
            assertThat(getLine(writer, 1), matches(elementStart + ">"));
            assertThat(getLine(writer, lines), matches("</(ns2:)?" + methodName + ">"));
        }
    }

    private void matchElement(String name, String value, int line) {
        assertThat(getLine(writer, line), matches("\\s*<" + name + ">" + value + "</" + name + ">"));
    }

    @Test
    public void testNoArgCall() throws Exception {
        testApi.noArgCall();

        assertThat(getLine(writer, 1), matches("<(ns2:)?noArgCall( xmlns:ns2=\"" + NS + "\")?/>"));
    }

    @Test
    public void testStringCall() throws Exception {
        testApi.stringCall("argValue");

        matchFrame("stringCall", 3);
        matchElement("argName", "argValue", 2);
    }

    @Test
    public void testIntegerCall() throws Exception {
        testApi.integerCall(345);

        matchFrame("integerCall", 3);
        matchElement("i", "345", 2);
    }

    @Test
    public void testNumberCall() throws Exception {
        testApi.numberCall(345);

        matchFrame("numberCall", 3);
        assertThat(getLine(writer, 2), matches("\\s*<numberName( xsi:type=\"xs:int\" "
                + "xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" "
                + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"| class=\"int\"|)" //
                + ">345</numberName>"));
    }

    @Test
    @Ignore
    // TODO handle namespaces
    public void testNamespaceCall() throws Exception {
        TestType t = new TestType("someValue");
        testApi.namespaceCall(t);

        assertEquals("<ns2:namespaceCall xmlns:ns2=\"" + NS + "\" xmlns:ns3=\"test-ns\">", getLine(
                writer, 1));
        assertEquals("    <theType>", getLine(writer, 2));
        assertEquals("        <ns3:value>someValue</ns3:value>", getLine(writer, 3));
        assertEquals("    </theType>", getLine(writer, 4));
        assertEquals("</ns2:namespaceCall>", getLine(writer, 5));
    }

    @Test
    public void testMultiCall() throws Exception {
        testApi.multiCall("strA", "strB");

        matchFrame("multiCall", 4);
        matchElement("a", "strA", 2);
        matchElement("b", "strB", 3);
    }

    @Test
    public void testOptionalCallWith() throws Exception {
        testApi.optionalCall("argValue");

        matchFrame("optionalCall", 3);
        matchElement("argName", "argValue", 2);
    }

    @Test
    public void testOptionalCallWithout() throws Exception {
        testApi.optionalCall(null);

        matchFrame("optionalCall", 2);
    }

    @Test
    public void testAmbiguousMethodNameCallString() throws Exception {
        testApi.ambiguousMethodName("strA");

        matchFrame("ambiguousMethodNameString", 3);
        matchElement("a", "strA", 2);
    }

    @Test
    public void testAmbiguousMethodNameCallInteger() throws Exception {
        testApi.ambiguousMethodName(123);

        matchFrame("ambiguousMethodNameInteger", 3);
        matchElement("a", "123", 2);
    }

    @Test
    public void testAmbiguousMethodNameCallInstant() throws Exception {
        testApi.ambiguousMethodName(Boolean.TRUE);

        matchFrame("ambiguousMethodNameBoolean", 3);
        matchElement("a", Boolean.TRUE.toString(), 2);
    }

    @Test
    public void testAmbiguousMethodNameCallStringString() throws Exception {
        testApi.ambiguousMethodName("strA", "strB");

        matchFrame("ambiguousMethodNameStringString", 4);
        matchElement("a", "strA", 2);
        matchElement("b", "strB", 3);
    }
}
