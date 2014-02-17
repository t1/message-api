package jmskata.receive;

import javax.ejb.*;
import javax.jms.*;

@MessageDriven(messageListenerInterface = MessageListener.class, //
activationConfig = { @ActivationConfigProperty(propertyName = "destination", propertyValue = "jmskata.messaging.CustomerService") })
public class ReceiverMdb implements MessageListener {
    @Override
    public void onMessage(Message inMessage) {
        try {
            MapMessage msg = (MapMessage) inMessage;
            String action = msg.getStringProperty("action");
            if ("CREATE".equals(action)) {
                String first = msg.getString("first");
                String last = msg.getString("last");

                System.out.println("customer service (0)");
                System.out.println("create customer:");
                System.out.println("first: " + first);
                System.out.println("last: " + last);
                System.out.println();
            } else if ("DELETE".equals(action)) {
                String id = msg.getString("id");

                System.out.println("customer service (0)");
                System.out.println("delete customer:");
                System.out.println("id: " + id);
                System.out.println();
            } else {
                System.out.println("error: unknown action: " + action);
            }
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }
}
