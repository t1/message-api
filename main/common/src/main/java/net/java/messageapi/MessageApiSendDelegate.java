package net.java.messageapi;

import javax.interceptor.InvocationContext;

public interface MessageApiSendDelegate {
    public void handle(InvocationContext ctx);
}
