package net.java.dev.messageapi.chat;

import java.util.*;

import javax.ejb.*;
import javax.inject.Named;

import net.java.dev.messageapi.ChatApi;

@Named
@Singleton
public class ChatReceiver implements ChatApi {

	private List<String> messages = new ArrayList<String>();

	@Override
	public void send(String message) {
		messages.add(message);
	}

	public List<String> getMessages() {
		return messages;
	}

	public void setMessages(List<String> messages) {
		this.messages = messages;
	}
}
