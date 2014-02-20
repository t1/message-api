package net.java.messageapi.tck.simple;

import net.java.messageapi.*;

@MessageApi
@DestinationName("queue/test")
public interface SimpleApi {
    public void simpleMethod(String simpleArg);
}
