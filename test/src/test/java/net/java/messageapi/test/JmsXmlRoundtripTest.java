package net.java.messageapi.test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import net.java.messageapi.adapter.xml.*;
import net.java.messageapi.test.TestApi;
import net.java.messageapi.test.defaultjaxb.JodaTimeApi;

import org.joda.time.Instant;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockejb.jms.TextMessageImpl;


@RunWith(Parameterized.class)
@Ignore
public class JmsXmlRoundtripTest extends AbstractJmsSenderFactoryTest {

    private final JaxbProvider jaxbProvider;

    @Parameters
    public static List<Object[]> data() {
        return Arrays.asList(new Object[][] { //
        { JaxbProvider.SUN_JDK },
        // TODO support: { JaxbProvider.ECLIPSE_LINK },
        // TODO support: { JaxbProvider.XSTREAM }
        });
    }

    public JmsXmlRoundtripTest(JaxbProvider jaxbProvider) {
        this.jaxbProvider = jaxbProvider;
    }

    private String instantCallXml(Instant now) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
                + "<ns2:instantCall xmlns:ns2=\"http://www.oneandone.com/consumer/tools/messaging\">\n"
                + "    <instantName>" + now + "</instantName>\n" //
                + "</ns2:instantCall>\n";
    }

    private String getMessagePayload() throws JMSException {
        return ((TextMessage) captureMessage()).getText();
    }

    @Test
    public void shouldCallServiceWhenSendingAsXmlMessage() throws JMSException {
        // TODO split into send and receive using MockEJB
        // Given
        TestApi service = JmsXmlSenderFactory.createFactory(TestApi.class, CONFIG, jaxbProvider).get();

        // When
        service.multiCall("a", "b");

        // Then
        String xml = getMessagePayload();
        TestApi serviceImpl = mock(TestApi.class);
        XmlStringDecoder.create(TestApi.class, serviceImpl).decode(xml);
        verify(serviceImpl).multiCall("a", "b");
    }

    @Test
    public void shouldSendUsingImplicitConversion() throws Exception {
        // Given
        Instant now = new Instant();
        JodaTimeApi service = JmsXmlSenderFactory.createFactory(JodaTimeApi.class, CONFIG, jaxbProvider).get();

        // When
        service.instantCall(now);

        assertEquals(instantCallXml(now), getMessagePayload());
    }

    @Test
    public void shouldReceiveUsingImplicitConversion() throws Exception {
        // Given
        Instant now = new Instant();
        String xml = instantCallXml(now);
        TextMessage textMessage = new TextMessageImpl(xml);
        JodaTimeApi serviceImpl = mock(JodaTimeApi.class);
        XmlMessageDecoder<JodaTimeApi> decoder = XmlMessageDecoder.create(JodaTimeApi.class,
                serviceImpl, jaxbProvider);

        // When
        decoder.onMessage(textMessage);

        // Then
        verify(serviceImpl).instantCall(now);
    }
}
