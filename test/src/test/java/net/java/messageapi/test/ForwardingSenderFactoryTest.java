package net.java.messageapi.test;

import static org.mockito.Mockito.*;
import net.java.messageapi.adapter.ForwardingSenderFactory;

import org.junit.Test;

public class ForwardingSenderFactoryTest {

    private final TestApi service = mock(TestApi.class);
    private final TestApi sender = ForwardingSenderFactory.create(TestApi.class, service);

    @Test
    public void shouldSendNoArgCall() throws Exception {
        sender.noArgCall();

        verify(service).noArgCall();
    }
}
