package jmskata.messaging;

import net.java.messageapi.MessageApi;

@MessageApi
public interface CustomerService {
    public void createCustomer(String first, String last);

    public void deleteCustomer(String id);
}
