package net.java.messageapi.test;

import net.java.messageapi.DestinationName;
import net.java.messageapi.MessageApi;

@MessageApi
@DestinationName("queue/test")
public interface NoConfigApi {
    public void noConfigCall();
}
