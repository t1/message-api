package jmskata.receive;

import javax.ejb.*;
import javax.jms.MessageListener;

import jmskata.messaging.CustomerService;
import net.java.messageapi.adapter.MessageDecoder;

@MessageDriven(messageListenerInterface = MessageListener.class, //
activationConfig = { @ActivationConfigProperty(propertyName = "destination", propertyValue = "jmskata.messaging.CustomerService") })
public class CustomerServiceImpl extends MessageDecoder<CustomerService> implements CustomerService {
    @Override
    public void createCustomer(String first, String last) {
        System.out.println("customer service (3)");
        System.out.println("create customer:");
        System.out.println("first: " + first);
        System.out.println("last: " + last);
        System.out.println();
    }

    @Override
    public void deleteCustomer(String id) {
        System.out.println("customer service (3)");
        System.out.println("delete customer:");
        System.out.println("id: " + id);
        System.out.println();
    }
}
