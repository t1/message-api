package net.java.messageapi.test;

import java.util.List;
import java.util.Set;

import net.java.messageapi.MessageApi;


@MessageApi
public interface SimpleCollectionsApi {
    public void stringListCall(List<String> listArg);

    public void testTypeListCall(List<TestType> list);

    public void stringSetCall(Set<String> setArg);
}
