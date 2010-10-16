package net.java.dev.messageapi.chat;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;

import net.java.dev.messageapi.ChatApi;

@Named
@Stateless
public class ChatControl {

    private static final List<String> messages = new ArrayList<String>();

    @Inject
    private ChatApi chat;

    public String getMessage() {
        return "enter your message here";
    }

    public void setMessage(String message) {
        chat.send(message);
    }

    public List<String> getMessages() {
        return messages;
    }

    public void receiveMessage(@Observes String message) {
        messages.add(message);
    }
}
