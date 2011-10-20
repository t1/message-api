package net.java.messageapi.adapter;

import static org.junit.Assert.*;

import java.lang.reflect.*;

import org.junit.BeforeClass;
import org.junit.Test;

public class MethodAsClassGeneratorTwoArgsTest {

    public interface TestInterfaceTwo {
        public void testMethodTwo(String foo, Integer bar);
    }

    private static Class<Object> generated;

    @BeforeClass
    public static void before() throws Exception {
        Method testMethod = TestInterfaceTwo.class.getMethod("testMethodTwo", String.class,
                Integer.class);
        MethodAsClassGenerator generator = new MethodAsClassGenerator(testMethod);

        generated = generator.generate();
    }

    @Test
    public void shouldBuildFields() throws Exception {
        Field[] declaredFields = generated.getDeclaredFields();
        assertEquals(2, declaredFields.length);

        Field field1 = declaredFields[0];
        assertEquals("arg0", field1.getName());
        assertEquals(0, field1.getModifiers()); // not public, etc.
        assertEquals(String.class, field1.getType());

        Field field2 = declaredFields[1];
        assertEquals("arg1", field2.getName());
        assertEquals(0, field2.getModifiers()); // not public, etc.
        assertEquals(Integer.class, field2.getType());
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
        assertEquals(1, declaredConstructors.length);
        Constructor<?> constructor = declaredConstructors[0];

        Class<?>[] parameterTypes = constructor.getParameterTypes();
        assertEquals(2, parameterTypes.length);
        assertEquals(String.class, parameterTypes[0]);
        assertEquals(Integer.class, parameterTypes[1]);
    }

    @Test
    public void constructorAndGetterShouldWork() throws Exception {
        Object instance = generated.getDeclaredConstructor(String.class, Integer.class).newInstance(
                "foo", 3);

        Method method1 = generated.getDeclaredMethod("getArg0");
        assertEquals("foo", method1.invoke(instance));

        Method method2 = generated.getDeclaredMethod("getArg1");
        assertEquals(3, method2.invoke(instance));
    }
}
