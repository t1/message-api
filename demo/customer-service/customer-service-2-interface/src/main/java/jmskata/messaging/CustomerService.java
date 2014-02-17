package jmskata.messaging;

public interface CustomerService {
    public void createCustomer(String first, String last);

    public void deleteCustomer(String id);
}
