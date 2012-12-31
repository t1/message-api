package net.java.messageapi.adapter;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class MapMessageDecoderTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    interface TestApi {
        public void testMethod(String string);
    }

    @Test
    public void shouldFailWithNullMapping() throws Exception {
        TestApi impl = new TestApi() {
            @Override
            public void testMethod(String string) {
                throw new UnsupportedOperationException();
            }
        };
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("mapping must not be null");
        new MapMessageDecoder<TestApi>(TestApi.class, impl, null).notify();
    }
}
