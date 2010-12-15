package net.java.messageapi;

import static org.junit.Assert.*;

import java.lang.reflect.Method;

import net.java.messageapi.reflection.Parameter;

import org.junit.Test;


public class PrimitiveParameterTest {

    public interface PrimitiveBooleanParameter {
        public void method(boolean b);
    }

    @Test
    public void shouldAssignPrimitiveBoolean() throws Exception {
        Method method = PrimitiveBooleanParameter.class.getMethod("method", Boolean.TYPE);
        Parameter parameter = Parameter.allOf(method).get(0);

        assertTrue(parameter.isCallable(Boolean.TRUE));
        assertTrue(parameter.isCallable(true));
    }

    public interface PrimitiveByteParameter {
        public void method(byte b);
    }

    @Test
    public void shouldAssignPrimitiveByte() throws Exception {
        Method method = PrimitiveByteParameter.class.getMethod("method", Byte.TYPE);
        Parameter parameter = Parameter.allOf(method).get(0);

        assertTrue(parameter.isCallable((byte) 123));
    }

    public interface PrimitiveCharacterParameter {
        public void method(char b);
    }

    @Test
    public void shouldAssignPrimitiveCharacter() throws Exception {
        Method method = PrimitiveCharacterParameter.class.getMethod("method", Character.TYPE);
        Parameter parameter = Parameter.allOf(method).get(0);

        assertTrue(parameter.isCallable('a'));
    }

    public interface PrimitiveShortParameter {
        public void method(short b);
    }

    @Test
    public void shouldAssignPrimitiveShort() throws Exception {
        Method method = PrimitiveShortParameter.class.getMethod("method", Short.TYPE);
        Parameter parameter = Parameter.allOf(method).get(0);

        assertTrue(parameter.isCallable((short) 123));
    }

    public interface PrimitiveIntegerParameter {
        public void method(int b);
    }

    @Test
    public void shouldAssignPrimitiveInteger() throws Exception {
        Method method = PrimitiveIntegerParameter.class.getMethod("method", Integer.TYPE);
        Parameter parameter = Parameter.allOf(method).get(0);

        assertTrue(parameter.isCallable(123));
    }

    public interface PrimitiveLongParameter {
        public void method(long b);
    }

    @Test
    public void shouldAssignPrimitiveLong() throws Exception {
        Method method = PrimitiveLongParameter.class.getMethod("method", Long.TYPE);
        Parameter parameter = Parameter.allOf(method).get(0);

        assertTrue(parameter.isCallable(123L));
    }

    public interface PrimitiveFloatParameter {
        public void method(float b);
    }

    @Test
    public void shouldAssignPrimitiveFloat() throws Exception {
        Method method = PrimitiveFloatParameter.class.getMethod("method", Float.TYPE);
        Parameter parameter = Parameter.allOf(method).get(0);

        assertTrue(parameter.isCallable(12.3f));
    }

    public interface PrimitiveDoubleParameter {
        public void method(double b);
    }

    @Test
    public void shouldAssignPrimitiveDouble() throws Exception {
        Method method = PrimitiveDoubleParameter.class.getMethod("method", Double.TYPE);
        Parameter parameter = Parameter.allOf(method).get(0);

        assertTrue(parameter.isCallable(12.34d));
    }
}
