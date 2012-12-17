package chat;

import java.util.*;

import javax.ejb.*;
import javax.jms.MessageListener;

import net.java.messageapi.adapter.MessageDecoder;

@MessageDriven(messageListenerInterface = MessageListener.class, //
activationConfig = { @ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/test") })
public class ChatReceiver extends MessageDecoder<ChatApi> implements ChatApi {

    static List<String> messages = new ArrayList<String>();

    @Override
    public void send(String message) {
        ChatReceiver.messages.add(message);
    }

    public List<String> getMessages() {
        return ChatReceiver.messages;
    }

    public void setMessages(List<String> messages) {
        ChatReceiver.messages = messages;
    }
}
