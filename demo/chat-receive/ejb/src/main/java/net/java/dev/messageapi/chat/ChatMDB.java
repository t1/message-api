package net.java.dev.messageapi.chat;

import javax.ejb.*;
import javax.jms.*;

import net.java.dev.messageapi.ChatApi;

import com.oneandone.consumer.messageapi.adapter.xml.XmlStringDecoder;

@MessageDriven(mappedName = "ChatTopic", activationConfig = {
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "topic/ChatTopic"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic") })
public class ChatMDB implements MessageListener {

	private final ChatApi receiver = new ChatReceiver();

	@Override
	public void onMessage(Message message) {
		String xml = getText(message);
		System.out.println("received: " + xml);
		XmlStringDecoder.create(ChatApi.class, receiver).decode(xml);
	}

	private String getText(Message message) {
		try {
			return ((TextMessage) message).getText();
		} catch (JMSException e) {
			throw new RuntimeException("can't get text", e);
		}
	}
}
