package net.java.messageapi.adapter;

import static java.util.concurrent.TimeUnit.*;
import static net.java.messageapi.adapter.Callback.*;
import static org.junit.Assert.*;

import java.util.concurrent.Semaphore;

import org.junit.Test;

public class CallbackTest {

    private static final Semaphore semaphoreIn = new Semaphore(0);
    private static final Semaphore semaphoreOut = new Semaphore(0);

    public interface CustomerService {
        public Long createCustomer(String first, String last);
    }

    public static CustomerService realService = new CustomerService() {
        @Override
        public Long createCustomer(String first, String last) {
            // wait for the main thread to release me
            try {
                assertTrue(semaphoreIn.tryAcquire(1, SECONDS));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return (long) (first + last).hashCode();
        }
    };

    private static final CustomerService service = forService(realService, CustomerService.class);

    private long createdCustomerId;

    @Test
    public void shouldReplyAsynchronously() throws Exception {
        replyTo(this).customerCreated(service.createCustomer("Joe", "Doe"));
        semaphoreIn.release(); // let the service thread continue

        assertTrue(semaphoreOut.tryAcquire(1, SECONDS)); // wait for the callback from the service
        assertEquals("JoeDoe".hashCode(), createdCustomerId);
    }

    @Test
    public void shouldWorkRepeatedly() throws Exception {
        replyTo(this).customerCreated(service.createCustomer("Joey", "Doey"));
        semaphoreIn.release(); // let the service thread continue

        assertTrue(semaphoreOut.tryAcquire(1, SECONDS)); // wait for the callback from the service
        assertEquals("JoeyDoey".hashCode(), createdCustomerId);
    }

    public void customerCreated(Long createdCustomerId) {
        this.createdCustomerId = createdCustomerId;
        semaphoreOut.release(); // let the main thread continue
    }
}
