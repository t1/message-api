package net.java.messageapi.chat;

import net.java.messageapi.MessageApi;

@MessageApi
public interface ChatApi {
	public void send(String message);
}