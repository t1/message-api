package net.java.messageapi.reflection;

import static org.junit.Assert.*;

import java.lang.reflect.Method;

import net.java.messageapi.JmsName;

import org.junit.Test;

public class JmsNameSupplierTest {

    static interface TestInterface {
        void testMethod(@JmsName("blub") String string0);
    }

    public JmsNameSupplier supplier = new JmsNameSupplier(null);

    @Test
    public void shouldFindJmsName() throws Exception {
        Method method = TestInterface.class.getMethod("testMethod", String.class);
        assertEquals("blub", supplier.get(new Parameter(method, 0)));
    }
}
