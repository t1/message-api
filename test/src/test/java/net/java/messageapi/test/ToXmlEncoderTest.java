package net.java.messageapi.test;

import static net.java.messageapi.test.RegexMatcher.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.*;

import org.junit.Test;

public class ToXmlEncoderTest {
    @Test
    public void shouldUseDefaultProvider() throws Exception {
        // Given
        Writer writer = new StringWriter();
        TestApi impl = ToXmlEncoderHelper.create(TestApi.class, writer);

        // When
        impl.noArgCall();

        // Then
        String[] line = writer.toString().split("\n");
        assertThat(line[0], matches("<\\?xml version=\"1.0\" encoding=\"UTF-8\"( standalone=\"yes\")?\\?>"));
        assertThat(line[1], is("<noArgCall/>"));
    }
}
