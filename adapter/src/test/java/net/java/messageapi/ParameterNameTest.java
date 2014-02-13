package net.java.messageapi;

import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.*;

import net.java.messageapi.reflection.Parameter;

import org.junit.Test;

public class ParameterNameTest {

    @Test
    public void shouldHaveNoParameters() throws Exception {
        Class<InterfaceWithParameterMapFile> type = InterfaceWithParameterMapFile.class;
        Method method = type.getMethod("methodWithNoArgs");

        Iterator<Parameter> parameters = Parameter.allOf(method).iterator();

        assertFalse(parameters.hasNext());
    }

    @Test
    public void shouldKnowOneParameterName() throws Exception {
        Class<InterfaceWithParameterMapFile> type = InterfaceWithParameterMapFile.class;
        Method method = type.getMethod("methodWithOneArg", String.class);

        Iterator<Parameter> parameters = Parameter.allOf(method).iterator();

        assertEquals("stringArg", parameters.next().getName());
        assertFalse(parameters.hasNext());
    }

    @Test
    public void shouldKnowTwoParameterNames() throws Exception {
        Class<InterfaceWithParameterMapFile> type = InterfaceWithParameterMapFile.class;
        Method method = type.getMethod("methodWithTwoArgs", String.class, Integer.class);

        Iterator<Parameter> parameters = Parameter.allOf(method).iterator();

        assertEquals("stringArg", parameters.next().getName());
        assertEquals("integerArg", parameters.next().getName());
        assertFalse(parameters.hasNext());
    }

    @Test
    public void shouldKnowThreeParameterNames() throws Exception {
        Class<InterfaceWithParameterMapFile> type = InterfaceWithParameterMapFile.class;
        Method method = type.getMethod("methodWithThreeArgs", String.class, Integer.class, Boolean.TYPE);

        Iterator<Parameter> parameters = Parameter.allOf(method).iterator();

        assertEquals("stringArg", parameters.next().getName());
        assertEquals("integerArg", parameters.next().getName());
        assertEquals("booleanArg", parameters.next().getName());
        assertFalse(parameters.hasNext());
    }

    @Test
    public void shouldKnowStringParameterNameFromAmbiguousMethod() throws Exception {
        Class<InterfaceWithParameterMapFile> type = InterfaceWithParameterMapFile.class;
        Method method = type.getMethod("ambiguousMethodWithOneArg", String.class);

        Iterator<Parameter> parameters = Parameter.allOf(method).iterator();

        assertEquals("stringArg", parameters.next().getName());
        assertFalse(parameters.hasNext());
    }

    @Test
    public void shouldKnowIntegerParameterNameFromAmbiguousMethod() throws Exception {
        Class<InterfaceWithParameterMapFile> type = InterfaceWithParameterMapFile.class;
        Method method = type.getMethod("ambiguousMethodWithOneArg", Integer.class);

        Iterator<Parameter> parameters = Parameter.allOf(method).iterator();

        assertEquals("integerArg", parameters.next().getName());
        assertFalse(parameters.hasNext());
    }

    @Test
    public void shouldKnowOneGenericParameterName() throws Exception {
        Class<InterfaceWithParameterMapFile> type = InterfaceWithParameterMapFile.class;
        Method method = type.getMethod("methodWithOneGenericArg", List.class);

        Iterator<Parameter> parameters = Parameter.allOf(method).iterator();

        assertEquals("listArg", parameters.next().getName());
        assertFalse(parameters.hasNext());
    }

    @Test
    public void shouldKnowOneNestedGenericParameterName() throws Exception {
        Class<InterfaceWithParameterMapFile> type = InterfaceWithParameterMapFile.class;
        Method method = type.getMethod("methodWithOneNestedGenericArg", Map.class);

        Iterator<Parameter> parameters = Parameter.allOf(method).iterator();

        assertEquals("listArg", parameters.next().getName());
        assertFalse(parameters.hasNext());
    }
}
