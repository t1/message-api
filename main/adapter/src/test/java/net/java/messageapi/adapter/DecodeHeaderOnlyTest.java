package net.java.messageapi.adapter;

import static org.mockito.Mockito.*;

import javax.jms.TextMessage;

import net.java.messageapi.JmsProperty;
import net.java.messageapi.Optional;
import net.sf.twip.TwiP;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(TwiP.class)
public class DecodeHeaderOnlyTest {

    public interface TestInterfaceHeaderOnly {
        public void testMethodHeaderOnly(@Optional String foo,
                @JmsProperty(headerOnly = true) Integer bar);
    }

    @Mock
    TestInterfaceHeaderOnly impl;
    @Mock
    TextMessage message;

    @Test
    public void shouldCopyJmsPropertyAnnotation() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
                + "<testMethodHeaderOnly>\n" //
                + "    <arg0>foo</arg0>\n" //
                + "</testMethodHeaderOnly>\n";
        when(message.getText()).thenReturn(xml);

        MessageDecoder<TestInterfaceHeaderOnly> decoder = MessageDecoder.of(
                TestInterfaceHeaderOnly.class, impl);

        decoder.onMessage(message);

        verify(impl).testMethodHeaderOnly("foo", null);
    }
}
