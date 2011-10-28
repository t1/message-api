package net.java.messageapi.test;

import java.util.*;

import net.java.messageapi.MessageApi;

@MessageApi
public interface ComplexCollectionsApi {
    public void stringStringMapCall(Map<String, String> mapArg);

    public void stringListSetCall(Set<List<String>> setList);

    public void stringListToStringMapCall(Map<String, List<String>> mapList);
}
