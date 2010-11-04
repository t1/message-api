package net.java.messageapi.test;

import net.java.messageapi.MessageApi;
import net.java.messageapi.Optional;

@MessageApi
public interface MappedApi {

    public void mappedCall(String s1, Long s2);

    public void optionalMappedCall(@Optional String string);

    public void mappedNoArgCall();
}
