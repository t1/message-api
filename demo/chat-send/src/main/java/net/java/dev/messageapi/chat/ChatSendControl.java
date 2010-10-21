package net.java.dev.messageapi.chat;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;

import net.java.dev.messageapi.ChatApi;

@Named
@Stateless
public class ChatSendControl {

	@Inject
	private ChatApi chat;

	public String getMessage() {
		return "enter your message here";
	}

	public void setMessage(String message) {
		chat.send(message);
	}
}
