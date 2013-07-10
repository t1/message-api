package net.java.messageapi.adapter;

import static org.junit.Assert.*;

import java.lang.reflect.Method;

import org.junit.Test;

public class InvocationProxyTest {

    private Method methodCalled;
    private Object[] argsCalled;

    @Test
    public void shouldInvokeSimpleClassMethod() throws Exception {
        new InvocationProxy<VersionSupplier>(VersionSupplier.class) {
            @Override
            public Object invoke(Method method, Object... args) {
                methodCalled = method;
                argsCalled = args;
                return null;
            }
        }.newInstance().getVersion(null);
        assertEquals("getVersion", methodCalled.getName());
        assertEquals(1, argsCalled.length);
    }

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
    }.newInstance();

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
    }.newInstance();

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
    }.newInstance();

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

    private static class PrivateClass {
        public void privateClassMethod() {
            throw new UnsupportedOperationException();
        }
    }

    @Test
    public void shouldFailWithPrivateClass() throws Exception {
        try {
            new InvocationProxy<PrivateClass>(PrivateClass.class) {
                @Override
                public Object invoke(Method method, Object... args) {
                    methodCalled = method;
                    argsCalled = args;
                    return null;
                }
            }.newInstance().privateClassMethod();
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            assertEquals(
                    "classes to be proxied must not be private: net.java.messageapi.adapter.InvocationProxyTest$PrivateClass",
                    e.getMessage());
        }
    }

    @Test
    public void shouldProxyAnonymousClass() throws Exception {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                throw new UnsupportedOperationException();
            }
        };

        @SuppressWarnings("unchecked")
        Class<Runnable> type = (Class<Runnable>) runnable.getClass();
        new InvocationProxy<Runnable>(type) {
            @Override
            public Object invoke(Method method, Object... args) {
                methodCalled = method;
                argsCalled = args;
                return null;
            }
        }.newInstance().run();

        assertEquals("run", methodCalled.getName());
        assertEquals(0, argsCalled.length);
    }

    class InnerClass {
        public void innerClassMethod() {
            throw new UnsupportedOperationException();
        }
    }

    @Test
    public void shouldFailWithInnerClass() throws Exception {
        try {
            new InvocationProxy<InnerClass>(InnerClass.class) {
                @Override
                public Object invoke(Method method, Object... args) {
                    methodCalled = method;
                    argsCalled = args;
                    return null;
                }
            }.newInstance().innerClassMethod();
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            assertEquals(
                    "inner classes can not be proxied. Use nested classes, i.e. make them static: net.java.messageapi.adapter.InvocationProxyTest$InnerClass",
                    e.getMessage());
        }
    }

    @Test
    public void shouldFailWithLocalClass() throws Exception {
        class LocalClass {
            public void localClassMethod() {
                throw new UnsupportedOperationException();
            }
        }

        try {
            new InvocationProxy<LocalClass>(LocalClass.class) {
                @Override
                public Object invoke(Method method, Object... args) {
                    methodCalled = method;
                    argsCalled = args;
                    return null;
                }
            }.newInstance().localClassMethod();
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            assertEquals(
                    "classes to be proxied must not be local: net.java.messageapi.adapter.InvocationProxyTest$1LocalClass",
                    e.getMessage());
        }
    }

    final static class FinalClass {}

    @Test
    public void shouldFailForFinalClass() throws Exception {
        try {
            new InvocationProxy<FinalClass>(FinalClass.class) {
                @Override
                public Object invoke(Method method, Object... args) {
                    throw new UnsupportedOperationException();
                }
            }.newInstance();
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            assertEquals(
                    "classes to be proxied must not be final: net.java.messageapi.adapter.InvocationProxyTest$FinalClass",
                    e.getMessage());
        }
    }
}
