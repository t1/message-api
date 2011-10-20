package net.java.messageapi.adapter;

import static org.junit.Assert.*;

import java.lang.reflect.*;

import org.junit.BeforeClass;
import org.junit.Test;

public class MethodAsClassGeneratorOneArgTest {

    public interface TestInterfaceOne {
        public void testMethodOne(String foo);
    }

    private static Class<Object> generated;

    @BeforeClass
    public static void before() throws Exception {
        Method testMethod = TestInterfaceOne.class.getMethod("testMethodOne", String.class);
        MethodAsClassGenerator generator = new MethodAsClassGenerator(testMethod);

        generated = generator.generate();
    }

    @Test
    public void shouldBuildField() throws Exception {
        Field[] declaredFields = generated.getDeclaredFields();
        assertEquals(1, declaredFields.length);
        Field field = declaredFields[0];
        assertEquals("arg0", field.getName());
        assertEquals(0, field.getModifiers()); // not public, etc.
        assertEquals(String.class, field.getType());
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
    public void shouldBuildConstructor() throws Exception {
        Constructor<?>[] declaredConstructors = generated.getDeclaredConstructors();
        assertEquals(1, declaredConstructors.length);
        Constructor<?> constructor = declaredConstructors[0];
        assertEquals(1, constructor.getParameterTypes().length);
        assertEquals(String.class, constructor.getParameterTypes()[0]);
    }

    @Test
    public void constructorAndGetterShouldWork() throws Exception {
        Object instance = generated.getDeclaredConstructor(String.class).newInstance("foo");

        Method method = generated.getDeclaredMethod("getArg0");
        assertEquals("foo", method.invoke(instance));
    }
}
