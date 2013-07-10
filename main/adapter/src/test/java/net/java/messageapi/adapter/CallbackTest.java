package net.java.messageapi.adapter;

import static java.util.concurrent.TimeUnit.*;
import static net.java.messageapi.adapter.Callback.*;
import static org.junit.Assert.*;

import java.util.concurrent.Semaphore;

import org.junit.Test;

public class CallbackTest {

    // two semaphores to make really sure it's asynchronous
    private static final Semaphore IN = new Semaphore(0);
    private static final Semaphore OUT = new Semaphore(0);

    public interface CustomerService {
        public Long createCustomer(String first, String last);
    }

    public static CustomerService realService = new CustomerService() {
        @Override
        public Long createCustomer(String first, String last) {
            acquireIn();
            return (long) (first + last).hashCode();
        }

        /** wait for the main thread to release me */
        private void acquireIn() {
            try {
                assertTrue(IN.tryAcquire(1, SECONDS));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    };

    private static final CustomerService service = Callback.forService(realService);

    private long createdCustomerId;

    @Test
    public void shouldReplyAsynchronouslyToCreateCustomer() throws Exception {
        replyTo(this).customerCreated(service.createCustomer("Joe", "Doe"));
        IN.release(); // let the service thread continue

        assertTrue(OUT.tryAcquire(1, SECONDS)); // wait for the callback from the service
        assertEquals("JoeDoe".hashCode(), createdCustomerId);
    }

    public void customerCreated(Long createdCustomerId) {
        this.createdCustomerId = createdCustomerId;
        OUT.release(); // let the main thread continue
    }

    @Test
    public void shouldFailOutsideOfMethodCall() throws Exception {
        Long create = service.createCustomer("Joe", "Doe");
        System.out.println("------------------> " + create);
        replyTo(this).customerCreated(create);
        IN.release(); // let the service thread continue

        assertTrue(OUT.tryAcquire(1, SECONDS)); // wait for the callback from the service
        assertEquals("JoeDoe".hashCode(), createdCustomerId);
    }
}
