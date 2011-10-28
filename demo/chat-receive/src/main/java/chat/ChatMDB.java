package chat;

import javax.ejb.MessageDriven;
import javax.jms.*;

import net.java.messageapi.adapter.MessageDecoder;
import net.java.messageapi.adapter.PojoInvoker;
import net.java.messageapi.adapter.xml.XmlStringDecoder;

@MessageDriven(mappedName = "Chat")
public class ChatMDB extends MessageDecoder<ChatApi> {
	public ChatMDB() {
		super(ChatApi.class, new ChatReceiver());
	}
}
