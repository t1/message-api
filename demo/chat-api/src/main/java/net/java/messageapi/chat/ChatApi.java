package net.java.messageapi.chat;

import com.oneandone.consumer.messageapi.MessageApi;

@MessageApi
public interface ChatApi {
	public void send(String message);
}