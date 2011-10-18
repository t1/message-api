package net.java.messageapi.reflection;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;

public class MethodNameHelperTest {

    public void noArgs() {
        // intentionally left empty
    }

    public void oneArg(String foo) {
        // intentionally left empty
    }

    public void twoArgs(String foo, int bar) {
        // intentionally left empty
    }

    public interface WrapperInterface {
        public void interfaceOneArg(String foo);
    }

    public abstract class AbstractClass {
        public abstract void abstractOneArg(String abs);

        public void concreteOneArg(String conc) {
            // intentionally left empty
        }
    }

    private final ParameterNameSupplier delegate = mock(ParameterNameSupplier.class);
    private final DebugInfoParameterNameSupplier supplier = new DebugInfoParameterNameSupplier(
            delegate);

    private Method noArgs;
    private Method oneArg;
    private Method twoArgs;
    private Method interfaceOneArg;
    private Method abstractOneArg;
    private Method concreteOneArg;

    @Before
    public void before() throws NoSuchMethodException {
        noArgs = MethodNameHelperTest.class.getMethod("noArgs");
        oneArg = MethodNameHelperTest.class.getMethod("oneArg", String.class);
        twoArgs = MethodNameHelperTest.class.getMethod("twoArgs", String.class, Integer.TYPE);
        interfaceOneArg = WrapperInterface.class.getMethod("interfaceOneArg", String.class);
        abstractOneArg = AbstractClass.class.getMethod("abstractOneArg", String.class);
        concreteOneArg = AbstractClass.class.getMethod("concreteOneArg", String.class);
    }

    private String getParameterName(Method method, int index) {
        return supplier.get(method, index);
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowOnNegativeArgIndex() throws Exception {
        getParameterName(oneArg, -1);
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowOnTooBigArgIndex0() throws Exception {
        getParameterName(noArgs, 0);
    }

    @Test
    public void shouldFindOneArgName() throws Exception {
        assertEquals("foo", getParameterName(oneArg, 0));
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowOnTooBigArgIndex1() throws Exception {
        getParameterName(oneArg, 1);
    }

    @Test
    public void shouldFindFirst() throws Exception {
        assertEquals("foo", getParameterName(twoArgs, 0));
    }

    @Test
    public void shouldFindSecond() throws Exception {
        assertEquals("bar", getParameterName(twoArgs, 1));
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowOnTooBigArgIndex2() throws Exception {
        getParameterName(twoArgs, 2);
    }

    @Test
    public void shouldNotFindParameterNameFromInterfaceMethod() throws Exception {
        when(delegate.get(interfaceOneArg, 0)).thenReturn("from-mock");
        assertEquals("from-mock", getParameterName(interfaceOneArg, 0));
    }

    @Test
    public void shouldNotFindParameterNameFromAbstractMethod() throws Exception {
        when(delegate.get(abstractOneArg, 0)).thenReturn("from-mock");
        assertEquals("from-mock", getParameterName(abstractOneArg, 0));
    }

    @Test
    public void shouldFindParameterNameFromConcreteMethod() throws Exception {
        assertEquals("conc", getParameterName(concreteOneArg, 0));
    }
}
