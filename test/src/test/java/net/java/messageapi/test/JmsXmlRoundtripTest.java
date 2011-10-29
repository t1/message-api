package net.java.messageapi.test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import javax.jms.*;

import net.java.messageapi.adapter.*;
import net.java.messageapi.adapter.JaxbProvider.JaxbProviderMemento;
import net.java.messageapi.test.defaultjaxb.JodaTimeApi;
import net.sf.twip.*;

import org.joda.time.Instant;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockejb.jms.TextMessageImpl;

@RunWith(TwiP.class)
public class JmsXmlRoundtripTest extends AbstractJmsSenderFactoryTest {

    private final JaxbProviderMemento memento;

    // TODO support ECLIPSE_LINK when this bug is fixed:
    // https://bugs.eclipse.org/bugs/show_bug.cgi?id=327811
    public JmsXmlRoundtripTest(
            @NotNull @Assume("!= XSTREAM & != ECLIPSE_LINK") JaxbProvider jaxbProvider) {
        this.memento = jaxbProvider.setUp();
    }

    @After
    public void after() {
        memento.restore();
    }

    private String instantCallXml(Instant now) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
                + "<ns2:instantCall xmlns:ns2=\"http://messageapi.java.net\">\n"
                + "    <instantName>" + now + "</instantName>\n" //
                + "</ns2:instantCall>\n";
    }

    @Override
    public TextMessage captureMessage() {
        return (TextMessage) super.captureMessage();
    }

    private String getMessagePayload() throws JMSException {
        return captureMessage().getText();
    }

    @Test
    public void shouldCallServiceWhenSendingAsXmlMessage() throws JMSException {
        // TODO split into send and receive using MockEJB
        // Given
        TestApi service = MessageSender.of(TestApi.class);

        // When
        service.multiCall("a", "b");

        // Then
        String xml = getMessagePayload();
        TestApi serviceImpl = mock(TestApi.class);
        Object pojo = XmlStringDecoder.create(TestApi.class).decode(xml);
        PojoInvoker.of(TestApi.class, serviceImpl).invoke(pojo);
        verify(serviceImpl).multiCall("a", "b");
    }

    @Test
    public void shouldSetVersionHeader() throws JMSException {
        // Given
        TestApi service = MessageSender.of(TestApi.class);

        // When
        service.multiCall("a", "b");

        // Then
        Message message = captureMessage();
        assertEquals("?", message.getStringProperty("VERSION"));
    }

    @Test
    public void shouldSendUsingImplicitConversion() throws Exception {
        // Given
        Instant now = new Instant();
        JodaTimeApi service = MessageSender.of(JodaTimeApi.class);

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
                serviceImpl);

        // When
        decoder.onMessage(textMessage);

        // Then
        verify(serviceImpl).instantCall(now);
    }
}
