package net.java.messageapi;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

public class MessageApiSendDelegater {

    @Inject
    MessageApiSendDelegate delegate;

    @AroundInvoke
    public Object handle(InvocationContext ctx) {
        delegate.handle(ctx);
        return null;
    }
}
