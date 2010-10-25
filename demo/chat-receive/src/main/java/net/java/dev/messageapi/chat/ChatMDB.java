package net.java.dev.messageapi.chat;

import javax.ejb.*;
import javax.jms.*;

import net.java.dev.messageapi.ChatApi;

import com.oneandone.consumer.messageapi.adapter.xml.XmlStringDecoder;

@MessageDriven(mappedName = "topic/testTopic")
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ChatMDB implements MessageListener {
    @EJB
    ChatApi receiver;

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
