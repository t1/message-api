package net.java.messageapi.adapter;

import net.java.messageapi.MessageApi;

import com.google.common.base.Supplier;

/**
 * Supplies an implementation of a {@link MessageApi} interface that can be used to submit calls to
 * some messaging system.
 */
public interface MessageSenderFactory<T> extends Supplier<T> {
    // just a supplier
}
