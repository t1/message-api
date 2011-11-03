package net.java.messageapi.adapter;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.Hashtable;
import java.util.StringTokenizer;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;

import net.java.messageapi.*;
import net.sf.twip.TwiP;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

/**
 * This is actually an integration test within the adapter classes
 */
@RunWith(TwiP.class)
public class AdapterIntegrationTest {

    public interface FullRoundTripTestInterface {
        public void fullRoundTripMessage(String foo, @JmsProperty @Optional Integer bar);
    }

    public static class MockContextFactory implements InitialContextFactory {
        @Override
        public Context getInitialContext(Hashtable<?, ?> arg0) throws NamingException {
            return mockContext;
        }
    }

    @BeforeClass
    public static void beforeClass() {
        System.setProperty("java.naming.factory.initial", MockContextFactory.class.getName());
    }

    @AfterClass
    public static void afterClass() {
        System.clearProperty("java.naming.factory.initial");
    }

    private static final Context mockContext = mock(Context.class);

    private final FullRoundTripTestInterface sender = MessageSender.of(FullRoundTripTestInterface.class);

    @Mock
    private ConnectionFactory connectionFactory;
    @Mock
    private Connection connection;
    @Mock
    private Session session;
    @Mock
    private Destination destination;
    @Mock
    private MessageProducer messageProducer;
    @Mock
    private TextMessage message;
    @Mock
    private FullRoundTripTestInterface receiver;

    private static final String XML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
            + "<fullRoundTripMessage>\n" //
            + "    <arg0>fooo</arg0>\n" //
            + "</fullRoundTripMessage>\n";

    @Test
    public void shouldSend() throws Exception {
        when(mockContext.lookup(ConnectionFactoryName.DEFAULT)).thenReturn(connectionFactory);
        when(mockContext.lookup(FullRoundTripTestInterface.class.getName())).thenReturn(destination);

        when(connectionFactory.createConnection(null, null)).thenReturn(connection);
        when(connection.createSession(true, Session.AUTO_ACKNOWLEDGE)).thenReturn(session);
        when(session.createProducer(destination)).thenReturn(messageProducer);
        when(session.createTextMessage(anyString())).thenReturn(message);

        sender.fullRoundTripMessage("fooo", 123);

        verify(messageProducer).send(message);
        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        verify(session).createTextMessage(argument.capture());
        assertEquals(XML, argument.getValue());
        verify(message).setIntProperty("arg1", 123);
    }

    @Test
    public void shouldReceive() throws Exception {
        when(message.getText()).thenReturn(XML);
        when(message.getPropertyNames()).thenReturn(new StringTokenizer("arg1"));
        when(message.getIntProperty("arg1")).thenReturn(123);

        XmlMessageDecoder<FullRoundTripTestInterface> decoder = XmlMessageDecoder.of(
                FullRoundTripTestInterface.class, receiver);
        decoder.onMessage(message);

        verify(receiver).fullRoundTripMessage("fooo", 123);
    }
}