package com.oneandone.consumer.messageapi.test;

import com.oneandone.consumer.messageapi.MessageApi;
import com.oneandone.consumer.messageapi.Optional;

@MessageApi
public interface MappedApi {

    public void mappedCall(String s1, Long s2);

    public void optionalMappedCall(@Optional String string);

    public void mappedNoArgCall();
}
