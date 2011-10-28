package chat;

import javax.ejb.MessageDriven;

import net.java.messageapi.adapter.MessageDecoder;

@MessageDriven(mappedName = "Chat")
public class ChatMDB extends MessageDecoder<ChatApi> {
	public ChatMDB() {
		super(ChatApi.class, new ChatReceiver());
	}
}
