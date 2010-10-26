package net.java.dev.messageapi.chat;

import java.util.ArrayList;
import java.util.List;

import net.java.dev.messageapi.ChatApi;

public class ChatReceiver implements ChatApi {

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
