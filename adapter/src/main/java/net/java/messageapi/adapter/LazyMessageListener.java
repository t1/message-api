package net.java.messageapi.adapter;

import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * Creates a target {@link MessageListener} lazily.
 */
public abstract class LazyMessageListener implements MessageListener {

    /** lazy initialized; some requirements may have to be set before it can be created */
    private MessageListener target;

    @Override
    public void onMessage(Message message) {
        if (target == null) {
            target = createTargetListener();
        }

        target.onMessage(message);
    }

    protected abstract MessageListener createTargetListener();
}