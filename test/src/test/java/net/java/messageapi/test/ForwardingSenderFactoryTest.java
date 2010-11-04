package net.java.messageapi.test;

import static org.mockito.Mockito.*;
import net.java.messageapi.adapter.xml.ForwardingSenderFactory;
import net.java.messageapi.test.TestApi;
import net.sf.twip.TwiP;

import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(TwiP.class)
public class ForwardingSenderFactoryTest {

    private final TestApi service = mock(TestApi.class);
    private final TestApi sender = ForwardingSenderFactory.create(TestApi.class, service).get();

    @Test
    public void shouldSendNoArgCall() throws Exception {
        sender.noArgCall();

        verify(service).noArgCall();
    }
}
