package net.java.messageapi.test;

import static org.mockito.Mockito.*;

import java.util.*;

import net.java.messageapi.adapter.ForwardingSenderFactory;

import org.junit.Test;

import com.google.common.collect.*;

public class SimpleCollectionsApiTest {

    private final SimpleCollectionsApi service = mock(SimpleCollectionsApi.class);
    private final SimpleCollectionsApi sender = ForwardingSenderFactory.create(SimpleCollectionsApi.class, service);

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
