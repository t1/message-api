package net.java.messageapi.adapter;

import static org.junit.Assert.*;

import java.io.StringWriter;
import java.lang.reflect.*;

import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.*;

import org.junit.BeforeClass;
import org.junit.Test;

public class MethodAsClassGeneratorOneArgTest {

    public interface TestInterfaceOne {
        public void testMethodOne(String foo);
    }

    private static Class<?> generated;
    private static Constructor<?> constructor;

    @BeforeClass
    public static void before() throws Exception {
        Method testMethod = TestInterfaceOne.class.getMethod("testMethodOne", String.class);
        MethodAsClassGenerator generator = new MethodAsClassGenerator(testMethod);

        generated = generator.get();
        constructor = generated.getDeclaredConstructor(String.class);
    }

    @Test
    public void shouldAnnotateAsXmlRootElement() throws Exception {
        XmlRootElement xmlRootElement = generated.getAnnotation(XmlRootElement.class);
        assertNotNull(xmlRootElement);
    }

    @Test
    public void shouldAnnotateAsXmlTypeWithPropOrder() throws Exception {
        XmlType xmlType = generated.getAnnotation(XmlType.class);
        assertNotNull(xmlType);
        assertArrayEquals(new String[] { "arg0" }, xmlType.propOrder());
    }

    @Test
    public void shouldBuildField() throws Exception {
        Field[] declaredFields = generated.getDeclaredFields();
        assertEquals(1, declaredFields.length);
        Field field = declaredFields[0];
        assertEquals("arg0", field.getName());
        assertEquals(0, field.getModifiers()); // not public, etc.
        assertEquals(String.class, field.getType());
        XmlElement element = field.getAnnotation(XmlElement.class);
        assertTrue(element.required());
    }

    @Test
    public void shouldBuildGetter() throws Exception {
        Method[] declaredMethods = generated.getDeclaredMethods();
        assertEquals(1, declaredMethods.length);
        Method method = declaredMethods[0];
        assertEquals("getArg0", method.getName());
        assertTrue(Modifier.isPublic(method.getModifiers()));
        assertEquals(String.class, method.getReturnType());
        assertEquals(0, method.getParameterTypes().length);
    }

    @Test
    public void shouldBuildConstructors() throws Exception {
        Constructor<?>[] declaredConstructors = generated.getDeclaredConstructors();
        assertEquals(2, declaredConstructors.length);

        Constructor<?> defaultConstructor = declaredConstructors[0];
        assertEquals(0, defaultConstructor.getParameterTypes().length);
        assertTrue(Modifier.isPrivate(defaultConstructor.getModifiers()));

        Constructor<?> fullConstructor = declaredConstructors[1];
        assertEquals(1, fullConstructor.getParameterTypes().length);
        assertEquals(String.class, fullConstructor.getParameterTypes()[0]);
        assertTrue(Modifier.isPublic(fullConstructor.getModifiers()));
    }

    @Test
    public void getterShouldWork() throws Exception {
        Object instance = constructor.newInstance("foo");

        Method method = generated.getDeclaredMethod("getArg0");
        assertEquals("foo", method.invoke(instance));
    }

    @Test
    public void shouldSerialize() throws Exception {
        Object instance = constructor.newInstance("foo");

        StringWriter writer = new StringWriter();
        JAXB.marshal(instance, writer);
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
                + "<testMethodOne>\n" //
                + "    <arg0>foo</arg0>\n" //
                + "</testMethodOne>\n", writer.toString());
    }
}
