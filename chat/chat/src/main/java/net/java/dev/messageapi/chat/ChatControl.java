package net.java.dev.messageapi.chat;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Named;

import net.java.dev.messageapi.ChatApi;

import com.oneandone.consumer.messageapi.adapter.DefaultJmsConfigFactory;
import com.oneandone.consumer.messageapi.adapter.JmsConfig;
import com.oneandone.consumer.messageapi.adapter.xml.JmsXmlSenderFactory;

@Named
@Stateless
public class ChatControl {

	private static final JmsConfig CONFIG = DefaultJmsConfigFactory.getJmsConfig("java:/JmsXA", "Chat",
			"admin", "admin");

	private final List<String> messages = new ArrayList<String>();

	private final ChatApi chat = JmsXmlSenderFactory.createProxy(ChatApi.class, CONFIG);

	public String getMessage() {
		return "enter your message here";
	}

	public void setMessage(String message) {
		messages.add(message);
		chat.send(message);
	}

	public List<String> getMessages() {
		return messages;
	}
}
