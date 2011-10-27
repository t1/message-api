package net.java.messageapi.adapter;

import static org.junit.Assert.*;

import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import javax.xml.bind.JAXB;

import org.junit.BeforeClass;
import org.junit.Test;

public class MethodAsClassGeneratorTwoMethodsTest {

    public interface TestInterfaceOne {
        public void sameMethodSignature(String foo);
    }

    public interface TestInterfaceTwo {
        public void sameMethodSignature(String foo);
    }

    private static Class<?> generatedOne;
    private static Constructor<?> constructorOne;
    private static Class<?> generatedTwo;
    private static Constructor<?> constructorTwo;

    @BeforeClass
    public static void before() throws Exception {
        Method testMethodOne = TestInterfaceOne.class.getMethod("sameMethodSignature", String.class);
        MethodAsClassGenerator generatorOne = new MethodAsClassGenerator(testMethodOne);
        generatedOne = generatorOne.get();
        constructorOne = generatedOne.getDeclaredConstructor(String.class);

        Method testMethodTwo = TestInterfaceTwo.class.getMethod("sameMethodSignature", String.class);
        MethodAsClassGenerator generatorTwo = new MethodAsClassGenerator(testMethodTwo);
        generatedTwo = generatorTwo.get();
        constructorTwo = generatedTwo.getDeclaredConstructor(String.class);
    }

    @Test
    public void shouldSerializeOne() throws Exception {
        Object instance = constructorOne.newInstance("foo");

        StringWriter writer = new StringWriter();
        JAXB.marshal(instance, writer);
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
                + "<sameMethodSignature>\n" //
                + "    <arg0>foo</arg0>\n" //
                + "</sameMethodSignature>\n", writer.toString());
    }

    @Test
    public void shouldSerializeTwo() throws Exception {
        Object instance = constructorTwo.newInstance("foo");

        StringWriter writer = new StringWriter();
        JAXB.marshal(instance, writer);
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
                + "<sameMethodSignature>\n" //
                + "    <arg0>foo</arg0>\n" //
                + "</sameMethodSignature>\n", writer.toString());
    }
}
