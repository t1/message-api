package net.java.messageapi.adapter;

import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Field;

import javax.enterprise.event.Event;
import javax.jms.*;
import javax.xml.bind.*;

import net.java.messageapi.DynamicDestinationName;

public class EventDecoder<T> implements MessageListener {
    private final Class<T> type;
    private final Event<T> event;
    private final JAXBContext context;

    public EventDecoder(Class<T> type, Event<T> createEvent) {
        this.event = createEvent;
        this.type = type;
        this.context = JaxbProvider.UNCHANGED.createJaxbContextFor(type);
    }

    @Override
    public void onMessage(Message message) {
        // FIXME handle other message types
        TextMessage textMessage = (TextMessage) message;
        T pojo = decode(getText(textMessage));
        JmsPropertiesFromMessageToPojo.scan(message, pojo);
        scanDynamicDestination(pojo, message);
        event.fire(pojo);
    }

    private String getText(TextMessage textMessage) {
        try {
            return textMessage.getText();
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }

    private T decode(String xml) {
        Reader reader = new StringReader(xml);
        return readPojo(reader);
    }

    private T readPojo(Reader reader) {
        try {
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return type.cast(unmarshaller.unmarshal(reader));
        } catch (JAXBException e) {
            throw new RuntimeException("can't unmarshal", e);
        }
    }

    private void scanDynamicDestination(T pojo, Message message) {
        // TODO scan recursively
        // TODO join with EventObserverSendAdapter
        for (Field field : pojo.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(DynamicDestinationName.class)) {
                field.setAccessible(true);
                try {
                    field.set(pojo, getDestinationOf(message));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private String getDestinationOf(Message message) {
        try {
            return message.getJMSDestination().toString();
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }
}
