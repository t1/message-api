package com.oneandone.consumer.messageapi.test;

import java.util.List;
import java.util.Set;

import com.oneandone.consumer.messageapi.MessageApi;

@MessageApi
public interface SimpleCollectionsApi {
    public void stringListCall(List<String> listArg);

    public void testTypeListCall(List<TestType> list);

    public void stringSetCall(Set<String> setArg);
}
