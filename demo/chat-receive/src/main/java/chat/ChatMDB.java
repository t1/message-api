package chat;

import javax.ejb.MessageDriven;
import javax.jms.*;

import net.java.messageapi.adapter.xml.XmlStringDecoder;

@MessageDriven(mappedName = "Chat")
public class ChatMDB implements MessageListener {

	private final ChatApi receiver = new ChatReceiver();

	@Override
	public void onMessage(Message message) {
		String xml = getText(message);
		XmlStringDecoder.create(ChatApi.class, receiver).decode(xml);
	}

	private String getText(Message message) {
		try {
			return ((TextMessage) message).getText();
		} catch (JMSException e) {
			throw new RuntimeException("can't get text", e);
		}
	}
}
