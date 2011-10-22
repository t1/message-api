package net.java.messageapi.adapter;

import static org.junit.Assert.*;

import java.io.StringWriter;
import java.lang.reflect.*;

import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.*;

import net.java.messageapi.JmsProperty;
import net.java.messageapi.Optional;

import org.junit.BeforeClass;
import org.junit.Test;

public class MethodAsClassGeneratorHeaderOnlyTest {

    public interface TestInterfaceHeaderOnly {
        public void testMethodHeaderOnly(@Optional String foo,
                @JmsProperty(headerOnly = true) Integer bar);
    }

    private static Class<?> generated;
    private static Constructor<?> constructor;

    @BeforeClass
    public static void before() throws Exception {
        Method testMethod = TestInterfaceHeaderOnly.class.getMethod("testMethodHeaderOnly",
                String.class, Integer.class);
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

        verify(declaredFields, 0, String.class, false, false);
        verify(declaredFields, 1, Integer.class, true, true);
    }

    private void verify(Field[] declaredFields, int index, Class<?> type, boolean required,
            boolean isTransient) {
        Field field = declaredFields[index];
        assertEquals("arg" + index, field.getName());
        assertEquals(0, field.getModifiers()); // not public, etc.
        assertEquals(type, field.getType());
        XmlElement element = field.getAnnotation(XmlElement.class);
        if (isTransient) {
            assertNull(element);
        } else {
            assertEquals(required, element.required());
        }

        XmlTransient xmlTransient = field.getAnnotation(XmlTransient.class);
        assertEquals(isTransient, xmlTransient != null);
    }

    @Test
    public void shouldBuildGetters() throws Exception {
        Method[] declaredMethods = generated.getDeclaredMethods();
        assertEquals(2, declaredMethods.length);

        verify(declaredMethods, 0, String.class);
        verify(declaredMethods, 1, Integer.class);
    }

    private void verify(Method[] declaredMethods, int index, Class<?> type) {
        Method method = declaredMethods[index];
        assertEquals("getArg" + index, method.getName());
        assertTrue(Modifier.isPublic(method.getModifiers()));
        assertEquals(type, method.getReturnType());
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
                + "<testMethodHeaderOnly>\n" //
                + "    <arg0>foo</arg0>\n" //
                + "</testMethodHeaderOnly>\n", writer.toString());
    }

    @Test
    public void shouldCopyJmsPropertyAnnotation() throws Exception {
        Field[] declaredFields = generated.getDeclaredFields();
        assertEquals(2, declaredFields.length);

        Field field = declaredFields[1];
        assertEquals("arg1", field.getName()); // just to make sure
        JmsProperty jmsProperty = field.getAnnotation(JmsProperty.class);
        assertNotNull(jmsProperty);
        assertEquals(true, jmsProperty.headerOnly());
    }
}
