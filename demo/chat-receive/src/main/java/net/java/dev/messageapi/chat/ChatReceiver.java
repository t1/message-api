package net.java.dev.messageapi.chat;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Named;

import net.java.dev.messageapi.ChatApi;

@Named
@Stateless
public class ChatReceiver implements ChatApi {

    private static List<String> messages = new ArrayList<String>();

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
