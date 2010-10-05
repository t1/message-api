package com.oneandone.consumer.messageapi.adapter;

import com.google.common.base.Supplier;
import com.oneandone.consumer.messageapi.MessageApi;

/**
 * Supplies an implementation of a {@link MessageApi} interface that can be used to submit calls to
 * some messaging system.
 */
public interface MessageSenderFactory<T> extends Supplier<T> {
    // just a supplier
}
