package com.oneandone.consumer.messageapi.test;

import static org.mockito.Mockito.*;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.junit.After;
import org.junit.Before;
import org.mockejb.MDBDescriptor;
import org.mockejb.MockContainer;
import org.mockejb.jndi.MockContextFactory;
import org.mockito.ArgumentCaptor;

import com.oneandone.consumer.messageapi.adapter.DefaultJmsConfigFactory;
import com.oneandone.consumer.messageapi.adapter.JmsConfig;

public abstract class AbstractJmsSenderFactoryTest {

    protected static final String FACTORY = "java:/JmsXA";
    protected static final String QUEUE = "MyQueueName";
    protected static final String QUEUE_PASS = "MyQueuePass";
    protected static final String QUEUE_USER = "MyQueueUser";

    protected static final JmsConfig CONFIG = DefaultJmsConfigFactory.getJmsConfig(FACTORY, QUEUE,
            QUEUE_USER, QUEUE_PASS);

    protected final MessageListener targetMDB = mock(MessageListener.class);

    @Before
    public void setAsInitialAndDeployMDB() throws NamingException, JMSException {
        MockContextFactory.setAsInitial();

        InitialContext context = new InitialContext();
        MockContainer mockContainer = new MockContainer(context);

        mockContainer.deploy(new MDBDescriptor(CONFIG.getFactoryName(), CONFIG.getDestinationName(),
                targetMDB));
    }

    @After
    public void unsetAsInitial() {
        MockContextFactory.revertSetAsInitial();
    }

    protected Message captureMessage() {
        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(targetMDB).onMessage(messageCaptor.capture());
        return messageCaptor.getValue();
    }
}
