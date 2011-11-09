package net.java.messageapi.test;

import static org.mockito.Mockito.*;

import java.util.*;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import net.java.messageapi.adapter.JmsQueueConfig;

import org.junit.*;
import org.mockejb.MDBDescriptor;
import org.mockejb.MockContainer;
import org.mockejb.jndi.MockContextFactory;
import org.mockito.ArgumentCaptor;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

public abstract class AbstractJmsSenderFactoryTest {

    protected static final String FACTORY = "java:/JmsXA";
    protected static final String QUEUE = "MyQueueName";
    protected static final String QUEUE_USER = "MyQueueUser";
    protected static final String QUEUE_PASS = "MyQueuePass";

    protected final JmsQueueConfig CONFIG = new JmsQueueConfig(FACTORY, QUEUE, QUEUE_USER,
            QUEUE_PASS, false, new Properties(), Collections.<String, Object> emptyMap());

    protected final MessageListener targetMDB = mock(MessageListener.class);

    @BeforeClass
    public static void disableTestLogs() throws JMSException {
        Logger mockEjb = (Logger) LoggerFactory.getLogger("org.mockejb");
        mockEjb.setLevel(Level.WARN);

        Logger messageTest = (Logger) LoggerFactory.getLogger("net.java.messageapi.test");
        messageTest.setLevel(Level.WARN);
    }

    @Before
    public void setAsInitialAndDeployMDB() throws NamingException, JMSException {
        MockContextFactory.setAsInitial();

        InitialContext context = new InitialContext();
        MockContainer mockContainer = new MockContainer(context);

        mockContainer.deploy(new MDBDescriptor(CONFIG.getFactoryName(),
                CONFIG.getDestinationName(), targetMDB));
    }

    protected Message captureMessage() {
        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(targetMDB).onMessage(messageCaptor.capture());
        return messageCaptor.getValue();
    }

    /** Helper for debugging */
    protected void printHeaders() {
        Message message = captureMessage();
        Enumeration<String> propertyNames = message.getPropertyNames();
        while (propertyNames.hasMoreElements()) {
            String propertyName = propertyNames.nextElement();
            System.out.println(propertyName + ": " + message.getStringProperty(propertyName));
        }
        System.out.println();
    }

    @After
    public void unsetAsInitial() {
        MockContextFactory.revertSetAsInitial();
    }
}
