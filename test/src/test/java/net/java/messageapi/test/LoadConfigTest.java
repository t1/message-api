package net.java.messageapi.test;

import static org.mockito.Mockito.*;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import net.java.messageapi.adapter.MessageSender;
import net.java.messageapi.adapter.xml.XmlStringDecoder;
import net.sf.twip.TwiP;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(TwiP.class)
public class LoadConfigTest extends AbstractJmsSenderFactoryTest {

    private String getMessagePayload() throws JMSException {
        return ((TextMessage) captureMessage()).getText();
    }

    @Test
    public void messageRoundtrip() throws JMSException {
        // Given
        TestApi serviceProxy = MessageSender.of(TestApi.class);
        TestApi serviceImpl = mock(TestApi.class);

        // When
        serviceProxy.multiCall("aaa", "bbb");
        // TODO wire the decoder to the mock JMS
        String xml = getMessagePayload();
        XmlStringDecoder.create(TestApi.class, serviceImpl).decode(xml);

        // Then
        verify(serviceImpl).multiCall("aaa", "bbb");
    }
}
