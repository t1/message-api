package net.java.dev.messageapi;

import com.oneandone.consumer.messageapi.MessageApi;

@MessageApi
public interface ChatApi {
	public void send(String message);
}