package net.java.dev.messageapi.chat;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.jms.*;

import net.java.dev.messageapi.ChatApi;

import com.oneandone.consumer.messageapi.adapter.xml.XmlStringDecoder;

@MessageDriven(activationConfig = { //
@ActivationConfigProperty(propertyName = "destination", propertyValue = "Chat") //
})
public class ChatListener implements MessageListener {

    private final XmlStringDecoder<ChatApi> decoder = //
    XmlStringDecoder.create(ChatApi.class, new ChatApi() {
        @Override
        public void send(String message) {
            event.fire(message);
        }
    });

    @Inject
    Event<String> event;

    @Override
    public void onMessage(Message message) {
        String xml = getText(message);
        decoder.decode(xml);
    }

    private String getText(Message message) {
        try {
            return ((TextMessage) message).getText();
        } catch (JMSException e) {
            throw new RuntimeException("can't get text", e);
        }
    }
}
