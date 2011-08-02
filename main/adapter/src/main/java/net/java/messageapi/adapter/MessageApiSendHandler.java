package net.java.messageapi.adapter;

import javax.interceptor.InvocationContext;

import net.java.messageapi.MessageApiSendDelegate;

public class MessageApiSendHandler implements MessageApiSendDelegate {
    public void handle(InvocationContext ctx) {
        Class<?> api = ctx.getMethod().getDeclaringClass();
        JmsSenderFactory factory = (JmsSenderFactory) MessageSender.getConfigFor(api);
        Object payload = factory.getPayloadHandler().toPayload(api, ctx.getMethod(),
                ctx.getParameters());
        factory.sendJms(api, payload);
    }
}
