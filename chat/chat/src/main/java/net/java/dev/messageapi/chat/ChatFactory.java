package net.java.dev.messageapi.chat;

import javax.enterprise.inject.Produces;

import net.java.dev.messageapi.ChatApi;

import com.oneandone.consumer.messageapi.adapter.DefaultJmsConfigFactory;
import com.oneandone.consumer.messageapi.adapter.JmsConfig;
import com.oneandone.consumer.messageapi.adapter.xml.JmsXmlSenderFactory;

public class ChatFactory {
    private static final JmsConfig CONFIG = DefaultJmsConfigFactory.getJmsConfig("java:/JmsXA",
            "Chat", "admin", "admin");

    @Produces
    ChatApi create() {
        return JmsXmlSenderFactory.createProxy(ChatApi.class, CONFIG);
    }
}
