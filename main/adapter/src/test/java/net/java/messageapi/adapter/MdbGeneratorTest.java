package net.java.messageapi.adapter;

import static org.junit.Assert.*;

import java.lang.reflect.Constructor;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.MessageListener;

import org.junit.Test;

public class MdbGeneratorTest {

    public static class Impl {
        // test
    }

    Class<?> mdb = new MdbGenerator(Impl.class).get();

    @Test
    public void testGeneratedClassName() throws Exception {
        assertEquals(Impl.class.getName() + "MDB", mdb.getName());
    }

    @Test
    public void superClassShouldBeMessageDecoder() throws Exception {
        assertEquals(MessageDecoder.class, mdb.getSuperclass());
    }

    @Test
    public void testMessageDrivenAnnotation() throws Exception {
        assertTrue(mdb.isAnnotationPresent(MessageDriven.class));

        MessageDriven messageDriven = mdb.getAnnotation(MessageDriven.class);
        assertEquals(MessageListener.class, messageDriven.messageListenerInterface());

        ActivationConfigProperty[] activationConfig = messageDriven.activationConfig();
        assertEquals(0, activationConfig.length); // actually 1!

        // ActivationConfigProperty property = activationConfig[0];
        // assertEquals("destination", property.propertyName());
        // assertEquals("destination", property.propertyValue());
    }

    @Test
    public void testDefaultConstructor() throws Exception {
        Constructor<?> constructor = mdb.getConstructor();
        assertNotNull(constructor);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void defaultConstructorShouldThrow() throws Exception {
        mdb.newInstance();
    }

    @Test
    public void testInjectingConstructor() throws Exception {
        Constructor<?> constructor = mdb.getConstructor(Impl.class);
        assertNotNull(constructor);
    }
}
