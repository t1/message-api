package net.java.messageapi.test;

import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Set;

import net.java.messageapi.adapter.xml.ForwardingSenderFactory;
import net.java.messageapi.adapter.xml.JaxbProvider;
import net.sf.twip.*;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

@RunWith(TwiP.class)
public class SimpleCollectionsApiTest {

    private final SimpleCollectionsApi service = mock(SimpleCollectionsApi.class);
    private final SimpleCollectionsApi sender;

    public SimpleCollectionsApiTest(@NotNull @Assume("!= XSTREAM") JaxbProvider provider) {
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
