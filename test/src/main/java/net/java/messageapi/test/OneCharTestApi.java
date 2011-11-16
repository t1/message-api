package net.java.messageapi.test;

import net.java.messageapi.JmsMappedPayload;
import net.java.messageapi.MessageApi;

@MessageApi
@JmsMappedPayload
public interface OneCharTestApi {
    public void a(String value);
}
