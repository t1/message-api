package net.java.messageapi.adapter;

import static org.junit.Assert.*;

import java.lang.reflect.Method;

import org.junit.Test;

public class InvocationProxyTest {

    private Method methodCalled;
    private Object[] argsCalled;

    public static class ProxiedClass {
        public void zeroArgNoReturnMethod() {
            throw new UnsupportedOperationException();
        }

        public void oneArgNoReturnMethod(String string0) {
            throw new UnsupportedOperationException();
        }

        public void twoArgsNoReturnMethod(String string0, String string1) {
            throw new UnsupportedOperationException();
        }

        public void threeArgsNoReturnMethod(String string0, String string1, String string2) {
            throw new UnsupportedOperationException();
        }
    }

    private final ProxiedClass classProxy = new InvocationProxy<ProxiedClass>(ProxiedClass.class) {
        @Override
        public Object invoke(Method method, Object... args) {
            methodCalled = method;
            argsCalled = args;
            return null;
        }
    }.cast();

    @Test
    public void shouldInvokeClassZeroArgMethod() throws Exception {
        classProxy.zeroArgNoReturnMethod();
        assertEquals("zeroArgNoReturnMethod", methodCalled.getName());
        assertEquals(0, argsCalled.length);
    }

    @Test
    public void shouldInvokeClassOneArgMethod() throws Exception {
        classProxy.oneArgNoReturnMethod("arg-0");
        assertEquals("oneArgNoReturnMethod", methodCalled.getName());
        assertArrayEquals(new Object[] { "arg-0" }, argsCalled);
    }

    @Test
    public void shouldInvokeClassTwoArgMethod() throws Exception {
        classProxy.twoArgsNoReturnMethod("arg-0", "arg-1");
        assertEquals("twoArgsNoReturnMethod", methodCalled.getName());
        assertArrayEquals(new Object[] { "arg-0", "arg-1" }, argsCalled);
    }

    @Test
    public void shouldInvokeClassThreeArgMethod() throws Exception {
        classProxy.threeArgsNoReturnMethod("arg-0", "arg-1", "arg-2");
        assertEquals("threeArgsNoReturnMethod", methodCalled.getName());
        assertArrayEquals(new Object[] { "arg-0", "arg-1", "arg-2" }, argsCalled);
    }

    public static class ProxiedInterface {
        public void zeroArgNoReturnMethod() {
            throw new UnsupportedOperationException();
        }

        public void oneArgNoReturnMethod(String string0) {
            throw new UnsupportedOperationException();
        }

        public void twoArgsNoReturnMethod(String string0, String string1) {
            throw new UnsupportedOperationException();
        }

        public void threeArgsNoReturnMethod(String string0, String string1, String string2) {
            throw new UnsupportedOperationException();
        }
    }

    private final ProxiedInterface interfaceProxy = new InvocationProxy<ProxiedInterface>(ProxiedInterface.class) {
        @Override
        public Object invoke(Method method, Object... args) {
            methodCalled = method;
            argsCalled = args;
            return null;
        }
    }.cast();

    @Test
    public void shouldInvokeInterfaceZeroArgMethod() throws Exception {
        interfaceProxy.zeroArgNoReturnMethod();
        assertEquals("zeroArgNoReturnMethod", methodCalled.getName());
        assertEquals(0, argsCalled.length);
    }

    @Test
    public void shouldInvokeInterfaceOneArgMethod() throws Exception {
        interfaceProxy.oneArgNoReturnMethod("arg-0");
        assertEquals("oneArgNoReturnMethod", methodCalled.getName());
        assertArrayEquals(new Object[] { "arg-0" }, argsCalled);
    }

    @Test
    public void shouldInvokeInterfaceTwoArgMethod() throws Exception {
        interfaceProxy.twoArgsNoReturnMethod("arg-0", "arg-1");
        assertEquals("twoArgsNoReturnMethod", methodCalled.getName());
        assertArrayEquals(new Object[] { "arg-0", "arg-1" }, argsCalled);
    }

    @Test
    public void shouldInvokeInterfaceThreeArgMethod() throws Exception {
        interfaceProxy.threeArgsNoReturnMethod("arg-0", "arg-1", "arg-2");
        assertEquals("threeArgsNoReturnMethod", methodCalled.getName());
        assertArrayEquals(new Object[] { "arg-0", "arg-1", "arg-2" }, argsCalled);
    }

    public static class ProxiedReturningClass {
        public Object objectReturningMethod() {
            throw new UnsupportedOperationException();
        }

        public String stringReturningMethod() {
            throw new UnsupportedOperationException();
        }
    }

    private final ProxiedReturningClass returningClassProxy = new InvocationProxy<ProxiedReturningClass>(
            ProxiedReturningClass.class) {
        @Override
        public Object invoke(Method method, Object... args) {
            methodCalled = method;
            argsCalled = args;
            return null;
        }
    }.cast();

    @Test
    public void shouldReturningObject() throws Exception {
        returningClassProxy.objectReturningMethod();
        assertEquals("objectReturningMethod", methodCalled.getName());
        assertEquals(0, argsCalled.length);
    }

    @Test
    public void shouldReturningString() throws Exception {
        returningClassProxy.stringReturningMethod();
        assertEquals("stringReturningMethod", methodCalled.getName());
        assertEquals(0, argsCalled.length);
    }
}
