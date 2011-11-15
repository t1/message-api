package net.java.messageapi.test;

import net.java.messageapi.JmsPayloadMapping;
import net.java.messageapi.MessageApi;

@MessageApi
@JmsPayloadMapping
public interface OneCharTestApi {
    public void a(String value);
}
