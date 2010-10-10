package net.java.dev.messageapi.quickstart;

import com.oneandone.consumer.messageapi.MessageApi;

@MessageApi
public interface ChatApi {
	public void send(String message);
}
