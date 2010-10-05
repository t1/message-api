package com.oneandone.consumer.messageapi;

import java.util.List;
import java.util.Map;

/* the parametermap file is not generated but is already in the resources path */
public interface InterfaceWithParameterMapFile {

    public void methodWithNoArgs();

    public void methodWithOneArg(String stringArg);

    public void methodWithTwoArgs(String stringArg, Integer integerArg);

    public void methodWithThreeArgs(String stringArg, Integer integerArg, boolean booleanArg);

    public void ambiguousMethodWithOneArg(String stringArg);

    public void ambiguousMethodWithOneArg(Integer integerArg);

    public void methodWithOneGenericArg(List<String> listArg);

    public void methodWithOneNestedGenericArg(Map<String, List<String>> listArg);
}
