package net.java.messageapi.adapter;

/**
 * The central provider for the proxies to send messages.
 * 
 * @see net.java.messageapi.MessageApi
 */
public class MessageSender {
    private MessageSender() {
        // this is just a singleton
    }

    public static <T> T of(Class<T> api) {
        return JmsConfig.getConfigFor(api).createProxy(api);
    }
}
