package com.oneandone.consumer.messageapi.adapter;

import static org.mockito.Mockito.*;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import net.sf.twip.TwiP;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(TwiP.class)
public class AsynchronousMockTest {

    @Test
    public void callDirectlySynchonousMock() throws Exception {
        final Runnable callback = mock(Runnable.class);

        new Runnable() {
            @Override
            public void run() {
                callback.run();
            }
        }.run();

        verify(callback).run();
    }

    @Test
    public void callDirectlyAsynchronousMock() throws Exception {
        final Semaphore semaphore = new Semaphore(0);
        Runnable callback = new Runnable() {
            @Override
            public void run() {
                try {
                    if (!semaphore.tryAcquire(100L, TimeUnit.MILLISECONDS)) {
                        throw new RuntimeException("could not acquire");
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        new AsynchronousMock(callback).run();

        semaphore.release();
    }
}
