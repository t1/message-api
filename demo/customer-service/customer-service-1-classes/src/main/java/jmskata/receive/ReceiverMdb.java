package jmskata.receive;

import javax.ejb.*;
import javax.inject.Inject;
import javax.jms.*;

@MessageDriven(messageListenerInterface = MessageListener.class, //
activationConfig = { @ActivationConfigProperty(propertyName = "destination", propertyValue = "jmskata.messaging.CustomerService") })
public class ReceiverMdb implements MessageListener {
    @Inject
    private CustomerServiceImpl customerServiceImpl;

    @Override
    public void onMessage(Message inMessage) {
        try {
            MapMessage msg = (MapMessage) inMessage;
            String action = msg.getStringProperty("action");
            if ("CREATE".equals(action)) {
                String first = msg.getString("first");
                String last = msg.getString("last");

                customerServiceImpl.createCustomer(first, last);
            } else if ("DELETE".equals(action)) {
                String id = msg.getString("id");

                customerServiceImpl.deleteCustomer(id);
            } else {
                System.out.println("error: unknown action: " + action);
            }
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }
}
