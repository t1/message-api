package net.java.messageapi.reflection;

import static org.junit.Assert.*;

import java.lang.reflect.Method;

import org.junit.*;

public class ParameterTest {

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

    private Method noArgs;
    private Method oneArg;
    private Method twoArgs;
    private Method interfaceOneArg;
    private Method abstractOneArg;
    private Method concreteOneArg;

    @Before
    public void before() throws NoSuchMethodException {
        noArgs = ParameterTest.class.getMethod("noArgs");
        oneArg = ParameterTest.class.getMethod("oneArg", String.class);
        twoArgs = ParameterTest.class.getMethod("twoArgs", String.class, Integer.TYPE);
        interfaceOneArg = WrapperInterface.class.getMethod("interfaceOneArg", String.class);
        abstractOneArg = AbstractClass.class.getMethod("abstractOneArg", String.class);
        concreteOneArg = AbstractClass.class.getMethod("concreteOneArg", String.class);
    }

    @Test
    public void noargMethodShouldHaveNoParameters() throws Exception {
        assertEquals(0, Parameter.allOf(noArgs).size());
    }

    @Test
    public void shouldFindOneNameFromParameter() throws Exception {
        assertEquals("foo", Parameter.allOf(oneArg).get(0).getName());
    }

    @Test
    public void shouldFindFirstNameFromParameter() throws Exception {
        assertEquals("foo", Parameter.allOf(twoArgs).get(0).getName());
    }

    @Test
    public void shouldFindSecondNameFromParameter() throws Exception {
        assertEquals("bar", Parameter.allOf(twoArgs).get(1).getName());
    }

    @Test
    public void shouldNotFindArgumentNameFromInterface() throws Exception {
        assertEquals("arg0", Parameter.allOf(interfaceOneArg).get(0).getName());
    }

    @Test
    public void shouldNotFindParameterNameFromAbstractMethod() throws Exception {
        assertEquals("arg0", Parameter.allOf(abstractOneArg).get(0).getName());
    }

    @Test
    public void shouldFindParameterNameFromConcreteMethod() throws Exception {
        assertEquals("conc", Parameter.allOf(concreteOneArg).get(0).getName());
    }
}
