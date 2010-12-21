package net.java.messageapi.adapter;

import net.java.messageapi.MessageApi;

/**
 * Supplies an implementation of a {@link MessageApi} interface that can be used to submit calls to
 * some messaging system.
 */
public interface MessageSenderFactory {
    public <T> T create(Class<T> api);
}
