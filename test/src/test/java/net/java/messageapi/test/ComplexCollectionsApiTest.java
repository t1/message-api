package net.java.messageapi.test;

import static org.mockito.Mockito.*;

import java.util.*;

import net.java.messageapi.adapter.*;

import org.junit.Test;

import com.google.common.collect.*;

public class ComplexCollectionsApiTest {

    private final ComplexCollectionsApi service = mock(ComplexCollectionsApi.class);
    private final ComplexCollectionsApi sender;

    // TODO support: SUN_JDK
    public ComplexCollectionsApiTest() {
        sender = ForwardingSenderFactory.create(ComplexCollectionsApi.class, service, JaxbProvider.ECLIPSE_LINK);
    }

    @Test
    public void shouldCallWithMap() throws Exception {
        // given
        Map<String, String> map = ImmutableMap.of("one", "eins", "two", "zwei", "three", "drei");

        // when
        sender.stringStringMapCall(map);

        // then
        verify(service).stringStringMapCall(map);
    }

    @Test
    public void shouldCallWithNestedSetList() throws Exception {
        // given
        List<String> en = ImmutableList.of("one", "two", "three");
        List<String> de = ImmutableList.of("eins", "zwei", "drei");
        Set<List<String>> setList = ImmutableSet.of(en, de);

        // when
        sender.stringListSetCall(setList);

        // then
        verify(service).stringListSetCall(setList);
    }

    @Test
    public void shouldCallWithNestedMapList() throws Exception {
        // given
        List<String> en = ImmutableList.of("one", "two", "three");
        List<String> de = ImmutableList.of("eins", "zwei", "drei");
        Map<String, List<String>> mapList = ImmutableMap.of("en", en, "de", de);

        // when
        sender.stringListToStringMapCall(mapList);

        // then
        verify(service).stringListToStringMapCall(mapList);
    }
}
