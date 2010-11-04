package net.java.messageapi.test;

import static net.java.messageapi.test.RegexMatcher.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.StringWriter;
import java.io.Writer;

import net.java.messageapi.adapter.xml.JaxbProvider;
import net.java.messageapi.adapter.xml.ToXmlSenderFactory;
import net.java.messageapi.test.TestApi;
import net.sf.twip.TwiP;

import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(TwiP.class)
public class ToXmlSenderFactoryTest {
    @Test
    public void shouldUseAllProviders(JaxbProvider jaxbProvider) throws Exception {
        // Given
        Writer writer = new StringWriter();
        TestApi impl = ToXmlSenderFactory.create(TestApi.class, jaxbProvider, writer).get();

        // When
        impl.noArgCall();

        // Then
        String[] line = writer.toString().split("\n");
        assertThat(line[0],
                matches("<\\?xml version=\"1.0\" encoding=\"UTF-8\"( standalone=\"yes\")?\\?>"));
        assertThat(line[1], is("<noArgCall/>"));
    }

    @Test
    public void shouldUseDefaultProvider() throws Exception {
        // Given
        Writer writer = new StringWriter();
        TestApi impl = ToXmlSenderFactory.create(TestApi.class, writer).get();

        // When
        impl.noArgCall();

        // Then
        String[] line = writer.toString().split("\n");
        assertThat(line[0],
                matches("<\\?xml version=\"1.0\" encoding=\"UTF-8\"( standalone=\"yes\")?\\?>"));
        assertThat(line[1], is("<noArgCall/>"));
    }
}
