package net.java.messageapi.adapter;

import static org.junit.Assert.*;

import org.junit.*;
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

        try {
            new MapMessageDecoder<TestApi>(TestApi.class, impl, null).notify();
            fail("expected RuntimeException");
        } catch (RuntimeException e) {
            assertEquals("mapping must not be null", e.getMessage());
        }
    }
}
