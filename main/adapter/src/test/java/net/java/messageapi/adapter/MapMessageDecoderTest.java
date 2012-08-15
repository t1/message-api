package net.java.messageapi.adapter;

import static org.junit.Assert.*;

import org.junit.Test;

public class MapMessageDecoderTest {

    interface TestApi {
        public void testMethod(String string);
    }

    @Test(expected = RuntimeException.class)
    public void shouldFailWithNullMapping() throws Exception {
        TestApi impl = new TestApi() {
            @Override
            public void testMethod(String string) {
                throw new UnsupportedOperationException();
            }
        };
        MapMessageDecoder<TestApi> decoder = new MapMessageDecoder<TestApi>(TestApi.class, impl, null);
        fail("should have failed");
        System.out.println(decoder);
    }
}
