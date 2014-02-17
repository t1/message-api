package jmskata.send;

import javax.annotation.Resource;
import javax.jms.*;

public class CustomerServiceSender {
    @Resource(mappedName = "java:/ConnectionFactory")
    private ConnectionFactory connectionFactory;
    @Resource(mappedName = "java:/jmskata.messaging.CustomerService")
    private Destination destination;

    public void createCustomer(String first, String last) {
        Connection connection = null;

        try {
            connection = connectionFactory.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer messageProducer = session.createProducer(destination);
            MapMessage message = session.createMapMessage();

            message.setStringProperty("action", "CREATE");
            message.setString("first", first);
            message.setString("last", last);

            messageProducer.send(message);
        } catch (JMSException e) {
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                    // ignore
                }
            }
        }
    }

    public void deleteCustomer(String id) {
        Connection connection = null;

        try {
            connection = connectionFactory.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer messageProducer = session.createProducer(destination);
            MapMessage message = session.createMapMessage();

            message.setStringProperty("action", "DELETE");
            message.setString("id", id);

            messageProducer.send(message);
        } catch (JMSException e) {
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                    // ignore
                }
            }
        }
    }
}
