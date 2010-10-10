package net.java.dev.messageapi.quickstart;

import java.util.*;

import javax.ejb.Stateless;
import javax.inject.*;

@Named
@Stateless
public class ChatControl {

	private final List<String> messages = new ArrayList<String>();

	@Inject
	ChatApi chat;

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
