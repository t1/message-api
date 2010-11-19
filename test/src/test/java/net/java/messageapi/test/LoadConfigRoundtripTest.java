package net.java.messageapi.test;

import static org.mockito.Mockito.*;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import net.java.messageapi.adapter.JmsConfig;
import net.java.messageapi.adapter.xml.XmlStringDecoder;
import net.sf.twip.TwiP;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(TwiP.class)
public class LoadConfigRoundtripTest extends AbstractJmsSenderFactoryTest {

    @Override
    protected JmsConfig createConfig() {
        return JmsConfig.getConfigFor(TestApi.class);
    }

    private String getMessagePayload() throws JMSException {
        return ((TextMessage) captureMessage()).getText();
    }

    @Test
    public void messageRoundtrip() throws JMSException {
        // Given
        TestApi serviceProxy = CONFIG.createProxy(TestApi.class);
        TestApi serviceImpl = mock(TestApi.class);

        // When
        serviceProxy.multiCall("a", "b");
        // TODO wire the decoder to the mock JMS
        String xml = getMessagePayload();
        XmlStringDecoder.create(TestApi.class, serviceImpl).decode(xml);

        // Then
        verify(serviceImpl).multiCall("a", "b");
    }
}
