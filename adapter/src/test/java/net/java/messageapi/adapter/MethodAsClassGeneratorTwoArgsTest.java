package net.java.messageapi.adapter;

import static org.junit.Assert.*;

import java.io.StringWriter;
import java.lang.reflect.*;

import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.*;

import net.java.messageapi.JmsProperty;
import net.java.messageapi.JmsOptional;

import org.junit.BeforeClass;
import org.junit.Test;

public class MethodAsClassGeneratorTwoArgsTest {

    public interface TestInterfaceTwo {
        public void testMethodTwo(String foo, @JmsProperty @JmsOptional Integer bar);
    }

    private static Class<?> generated;
    private static Constructor<?> constructor;

    @BeforeClass
    public static void before() throws Exception {
        Method testMethod = TestInterfaceTwo.class.getMethod("testMethodTwo", String.class, Integer.class);
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
    public void shouldAnnotateAsXmlTypeWithPropOrder() throws Exception {
        XmlType xmlType = generated.getAnnotation(XmlType.class);
        assertNotNull(xmlType);
        assertArrayEquals(new String[] { "arg0" }, xmlType.propOrder());
    }

    @Test
    public void shouldBuildFields() throws Exception {
        Field[] declaredFields = generated.getDeclaredFields();
        assertEquals(2, declaredFields.length);

        verify(declaredFields, 0, String.class, true, 0);
        verify(declaredFields, 1, Integer.class, false, Modifier.TRANSIENT);
    }

    private void verify(Field[] declaredFields, int index, Class<?> type, boolean required, int expectedModifiers) {
        Field field = declaredFields[index];
        assertEquals("arg" + index, field.getName());
        assertEquals(type, field.getType());
        if (required) {
            XmlElement element = field.getAnnotation(XmlElement.class);
            assertEquals(true, element.required());
        }
        assertEquals(expectedModifiers, field.getModifiers());
    }

    @Test
    public void shouldBuildGetters() throws Exception {
        assertEquals(2, generated.getDeclaredMethods().length);
        verify(0, String.class);
        verify(1, Integer.class);
    }

    private void verify(int index, Class<?> type) throws NoSuchMethodException {
        Method method = generated.getDeclaredMethod("getArg" + index);
        assertTrue(Modifier.isPublic(method.getModifiers()));
        assertEquals(type, method.getReturnType());
        assertEquals(0, method.getParameterTypes().length);
    }

    @Test
    public void shouldBuildConstructors() throws Exception {
        Constructor<?>[] declaredConstructors = generated.getDeclaredConstructors();
        assertEquals(2, declaredConstructors.length);

        // no guaranteed ordering :|
        for (Constructor<?> declaredConstructor : declaredConstructors) {
            // default constructor
            if (declaredConstructor.getParameterTypes().length == 0) {
                assertTrue(Modifier.isPrivate(declaredConstructor.getModifiers()));
            } else { // all args constructor
                Class<?>[] parameterTypes = declaredConstructor.getParameterTypes();
                assertEquals(2, parameterTypes.length);
                assertEquals(String.class, parameterTypes[0]);
                assertEquals(Integer.class, parameterTypes[1]);
                assertTrue(Modifier.isPublic(declaredConstructor.getModifiers()));
            }
        }
    }

    @Test
    public void getterShouldWork() throws Exception {
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
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" + "<testMethodTwo>\n" //
                + "    <arg0>foo</arg0>\n" //
                + "</testMethodTwo>\n", writer.toString());
    }

    @Test
    public void shouldCopyJmsPropertyAnnotation() throws Exception {
        Field[] declaredFields = generated.getDeclaredFields();
        assertEquals(2, declaredFields.length);

        Field field = declaredFields[1];
        assertEquals("arg1", field.getName()); // just to make sure
        assertTrue(field.isAnnotationPresent(JmsProperty.class));
    }
}
