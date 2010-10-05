package com.oneandone.consumer.messageapi.test;

import com.oneandone.consumer.messageapi.MessageApi;
import com.oneandone.consumer.messageapi.Optional;

@MessageApi
public interface TestApi {
    public void noArgCall();

    public void stringCall(String argName);

    public void integerCall(Integer i);

    public void numberCall(Number numberName);

    public void namespaceCall(TestType theType);

    public void arrayCall(String[] array);

    public void varargCall(String... varargs);

    public void multiCall(String a, String b);

    public void optionalCall(@Optional String argName);

    public void ambiguousMethodName(String a);

    public void ambiguousMethodName(Integer a);

    public void ambiguousMethodName(Boolean a);

    public void ambiguousMethodName(String a, String b);

    public enum NonExecutableSibling {
        ONE
    }
}
