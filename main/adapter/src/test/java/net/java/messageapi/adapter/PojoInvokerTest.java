package net.java.messageapi.adapter;

import static org.mockito.Mockito.*;

import javax.xml.bind.annotation.XmlType;

import org.junit.Test;

public class PojoInvokerTest {

    public interface NoArgApi {
        public void someMethod();
    }

    @Test
    public void shouldInvokeNoArgApi() throws Exception {
        NoArgApi impl = mock(NoArgApi.class);
        PojoInvoker<NoArgApi> invoker = PojoInvoker.of(NoArgApi.class, impl);
        class SomeMethod {
        }
        SomeMethod pojo = new SomeMethod();

        invoker.invoke(pojo);

        verify(impl).someMethod();
    }

    public interface OneStringApi {
        public void someMethod(String string);
    }

    @Test
    public void shouldInvokeOneStringApi() throws Exception {
        OneStringApi impl = mock(OneStringApi.class);
        PojoInvoker<OneStringApi> invoker = PojoInvoker.of(OneStringApi.class, impl);
        @SuppressWarnings("unused")
        class SomeMethod {
            String string;

            public String getString() {
                return string;
            }
        }
        SomeMethod pojo = new SomeMethod();
        pojo.string = "test";

        invoker.invoke(pojo);

        verify(impl).someMethod("test");
    }

    public interface TwoStringsApi {
        public void someMethod(String string1, String string2);
    }

    @Test
    public void shouldInvokeTwoStringsApi() throws Exception {
        TwoStringsApi impl = mock(TwoStringsApi.class);
        PojoInvoker<TwoStringsApi> invoker = PojoInvoker.of(TwoStringsApi.class, impl);
        @SuppressWarnings("unused")
        @PropOrder({ "string1", "string2" })
        class SomeMethod {
            String string1, string2;

            public String getString1() {
                return string1;
            }

            public String getString2() {
                return string2;
            }
        }
        SomeMethod pojo = new SomeMethod();
        pojo.string1 = "test1";
        pojo.string2 = "test2";

        invoker.invoke(pojo);

        verify(impl).someMethod("test1", "test2");
    }

    public interface IntegerAndStringApi {
        public void someMethod(int integer, String string);
    }

    @Test
    public void shouldInvokeIntegerAndStringApi() throws Exception {
        IntegerAndStringApi impl = mock(IntegerAndStringApi.class);
        PojoInvoker<IntegerAndStringApi> invoker = PojoInvoker.of(IntegerAndStringApi.class, impl);
        @SuppressWarnings("unused")
        @PropOrder({ "integer", "string" })
        class SomeMethod {
            int integer;
            String string;

            public int getInteger() {
                return integer;
            }

            public String getString() {
                return string;
            }
        }
        SomeMethod pojo = new SomeMethod();
        pojo.integer = 123;
        pojo.string = "test";

        invoker.invoke(pojo);

        verify(impl).someMethod(123, "test");
    }

    @Test
    public void shouldInvokeShuffledIntegerAndStringApi() throws Exception {
        IntegerAndStringApi impl = mock(IntegerAndStringApi.class);
        PojoInvoker<IntegerAndStringApi> invoker = PojoInvoker.of(IntegerAndStringApi.class, impl);
        @SuppressWarnings("unused")
        @XmlType(propOrder = { "integer", "string" })
        class SomeMethod {
            int integer;
            String string;

            public String getString() {
                return string;
            }

            public int getInteger() {
                return integer;
            }
        }
        SomeMethod pojo = new SomeMethod();
        pojo.integer = 123;
        pojo.string = "test";

        invoker.invoke(pojo);

        verify(impl).someMethod(123, "test");
    }

    public interface StringAndIntegerApi {
        public void someMethod(String string, int integer);
    }

    @Test
    public void shouldInvokeStringAndIntegerApi() throws Exception {
        StringAndIntegerApi impl = mock(StringAndIntegerApi.class);
        PojoInvoker<StringAndIntegerApi> invoker = PojoInvoker.of(StringAndIntegerApi.class, impl);
        @SuppressWarnings("unused")
        @XmlType(propOrder = { "string", "integer" })
        class SomeMethod {
            int integer;
            String string;

            public int getInteger() {
                return integer;
            }

            public String getString() {
                return string;
            }
        }
        SomeMethod pojo = new SomeMethod();
        pojo.integer = 123;
        pojo.string = "test";

        invoker.invoke(pojo);

        verify(impl).someMethod("test", 123);
    }

    public interface TwoOneStringMethodsApi {
        public void someMethod(String string);

        public void anotherMethod(String string);
    }

    @Test
    public void shouldInvokeFirstOfTwoOneStringMethodsApi() throws Exception {
        TwoOneStringMethodsApi impl = mock(TwoOneStringMethodsApi.class);
        PojoInvoker<TwoOneStringMethodsApi> invoker = PojoInvoker.of(TwoOneStringMethodsApi.class, impl);
        @SuppressWarnings("unused")
        class SomeMethod {
            String string;

            public String getString() {
                return string;
            }
        }
        SomeMethod pojo = new SomeMethod();
        pojo.string = "test";

        invoker.invoke(pojo);

        verify(impl).someMethod("test");
    }

    @Test
    public void shouldInvokeSecondOfTwoOneStringMethodsApi() throws Exception {
        TwoOneStringMethodsApi impl = mock(TwoOneStringMethodsApi.class);
        PojoInvoker<TwoOneStringMethodsApi> invoker = PojoInvoker.of(TwoOneStringMethodsApi.class, impl);
        @SuppressWarnings("unused")
        class AnotherMethod {
            String string;

            public String getString() {
                return string;
            }
        }
        AnotherMethod pojo = new AnotherMethod();
        pojo.string = "test";

        invoker.invoke(pojo);

        verify(impl).anotherMethod("test");
    }
}
