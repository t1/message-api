package net.java.messageapi.adapter;

import java.io.Reader;
import java.io.StringReader;

import javax.enterprise.event.Event;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;


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
		TextMessage textMessage = (TextMessage) message;
		T pojo = decode(getText(textMessage));
		JmsPropertiesFromMessageToPojo.scan(message, pojo);
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
}
