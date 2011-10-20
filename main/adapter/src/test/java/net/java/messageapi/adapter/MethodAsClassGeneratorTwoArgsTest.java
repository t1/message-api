package net.java.messageapi.adapter;

import static org.junit.Assert.*;

import java.io.StringWriter;
import java.lang.reflect.*;

import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import net.java.messageapi.JmsProperty;
import net.java.messageapi.Optional;

import org.junit.BeforeClass;
import org.junit.Test;

public class MethodAsClassGeneratorTwoArgsTest {

    public interface TestInterfaceTwo {
        public void testMethodTwo(String foo, @JmsProperty @Optional Integer bar);
    }

    private static Class<?> generated;
    private static Constructor<?> constructor;

    @BeforeClass
    public static void before() throws Exception {
        Method testMethod = TestInterfaceTwo.class.getMethod("testMethodTwo", String.class,
                Integer.class);
        MethodAsClassGenerator generator = new MethodAsClassGenerator(testMethod);

        generated = generator.get();
        constructor = generated.getDeclaredConstructor(String.class, Integer.class);
    }

    @Test
    public void shouldAnnotateAsXmlRootElement() throws Exception {
        XmlRootElement xmlRootElement = generated.getAnnotation(XmlRootElement.class);
        assertNotNull(xmlRootElement);
    }

    @Test
    public void shouldBuildFields() throws Exception {
        Field[] declaredFields = generated.getDeclaredFields();
        assertEquals(2, declaredFields.length);

        Field field1 = declaredFields[0];
        assertEquals("arg0", field1.getName());
        assertEquals(0, field1.getModifiers()); // not public, etc.
        assertEquals(String.class, field1.getType());
        XmlElement element1 = field1.getAnnotation(XmlElement.class);
        assertTrue(element1.required());

        Field field2 = declaredFields[1];
        assertEquals("arg1", field2.getName());
        assertEquals(0, field2.getModifiers()); // not public, etc.
        assertEquals(Integer.class, field2.getType());
        XmlElement element2 = field2.getAnnotation(XmlElement.class);
        assertFalse(element2.required());
    }

    @Test
    public void shouldBuildGetters() throws Exception {
        Method[] declaredMethods = generated.getDeclaredMethods();
        assertEquals(2, declaredMethods.length);

        Method method1 = declaredMethods[0];
        assertEquals("getArg0", method1.getName());
        assertTrue(Modifier.isPublic(method1.getModifiers()));
        assertEquals(String.class, method1.getReturnType());
        assertEquals(0, method1.getParameterTypes().length);

        Method method2 = declaredMethods[1];
        assertEquals("getArg1", method2.getName());
        assertTrue(Modifier.isPublic(method2.getModifiers()));
        assertEquals(Integer.class, method2.getReturnType());
        assertEquals(0, method2.getParameterTypes().length);
    }

    @Test
    public void shouldBuildConstructors() throws Exception {
        Constructor<?>[] declaredConstructors = generated.getDeclaredConstructors();
        assertEquals(2, declaredConstructors.length);

        Constructor<?> defaultConstructor = declaredConstructors[0];
        assertEquals(0, defaultConstructor.getParameterTypes().length);
        assertTrue(Modifier.isPrivate(defaultConstructor.getModifiers()));

        Constructor<?> fullConstructor = declaredConstructors[1];
        Class<?>[] parameterTypes = fullConstructor.getParameterTypes();
        assertEquals(2, parameterTypes.length);
        assertEquals(String.class, parameterTypes[0]);
        assertEquals(Integer.class, parameterTypes[1]);
        assertTrue(Modifier.isPublic(fullConstructor.getModifiers()));
    }

    @Test
    public void constructorAndGetterShouldWork() throws Exception {
        Object instance = constructor.newInstance("foo", 3);

        Method method1 = generated.getDeclaredMethod("getArg0");
        assertEquals("foo", method1.invoke(instance));

        Method method2 = generated.getDeclaredMethod("getArg1");
        assertEquals(3, method2.invoke(instance));
    }

    @Test
    public void shouldSerialize() throws Exception {
        Object instance = constructor.newInstance("foo", 3);

        StringWriter writer = new StringWriter();
        JAXB.marshal(instance, writer);
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
                + "<testMethodTwo>\n" //
                + "    <arg0>foo</arg0>\n" //
                + "    <arg1>3</arg1>\n" //
                + "</testMethodTwo>\n", writer.toString());
    }
}
