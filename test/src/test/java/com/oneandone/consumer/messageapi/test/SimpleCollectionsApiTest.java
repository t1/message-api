package com.oneandone.consumer.messageapi.test;

import static org.mockito.Mockito.*;

import java.util.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.oneandone.consumer.messageapi.adapter.xml.ForwardingSenderFactory;
import com.oneandone.consumer.messageapi.adapter.xml.JaxbProvider;

@RunWith(Parameterized.class)
public class SimpleCollectionsApiTest {

    @Parameters
    public static List<Object[]> data() {
        return Arrays.asList(new Object[][] { //
        { JaxbProvider.SUN_JDK }, { JaxbProvider.ECLIPSE_LINK }
        // TODO support: { JaxbProvider.XSTREAM }
        });
    }

    private final SimpleCollectionsApi service = mock(SimpleCollectionsApi.class);
    private final SimpleCollectionsApi sender;

    public SimpleCollectionsApiTest(JaxbProvider provider) {
        sender = ForwardingSenderFactory.create(SimpleCollectionsApi.class, service, provider).get();
    }

    @Test
    public void shouldCallWithList() throws Exception {
        // given
        List<String> list = ImmutableList.of("eins", "zwei", "drei");

        // when
        sender.stringListCall(list);

        // then
        verify(service).stringListCall(list);
    }

    @Test
    public void shouldCallWithSet() throws Exception {
        // given
        Set<String> set = ImmutableSet.of("eins", "zwei", "drei");

        // when
        sender.stringSetCall(set);

        // then
        verify(service).stringSetCall(set);
    }

    @Test
    public void shouldCallWithTestTypeList() throws Exception {
        // given
        List<TestType> testTypeList = ImmutableList.of(new TestType("one"), new TestType("two"));

        // when
        sender.testTypeListCall(testTypeList);

        // then
        verify(service).testTypeListCall(testTypeList);
    }
}
