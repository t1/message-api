package net.java.dev.messageapi.quickstart;

public class ChatImpl implements ChatApi {
	@Override
	public void send(String message) {
		System.out.println("send [" + message + "]");
	}
}
