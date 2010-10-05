package com.oneandone.consumer.messageapi.test;

import static org.mockito.Mockito.*;
import net.sf.twip.TwiP;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.consumer.messageapi.adapter.xml.ForwardingSenderFactory;

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
