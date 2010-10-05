package com.oneandone.consumer.messageapi.test.eclipselink;

import java.util.*;

import com.oneandone.consumer.messageapi.MessageApi;

/**
 * This api is in a separate package, so the Sun JAXB provider doesn't complain... only EclipseLink
 * can handle Maps and other complex types properly.
 */
@MessageApi
public interface ComplexCollectionsApi {
    public void stringStringMapCall(Map<String, String> mapArg);

    public void stringListSetCall(Set<List<String>> setList);

    public void stringListToStringMapCall(Map<String, List<String>> mapList);
}
