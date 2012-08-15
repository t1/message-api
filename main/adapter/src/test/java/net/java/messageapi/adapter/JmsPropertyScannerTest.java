package net.java.messageapi.adapter;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.util.*;

import net.java.messageapi.JmsProperty;
import net.sf.twip.TwiP;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.common.collect.*;

@RunWith(TwiP.class)
public class JmsPropertyScannerTest {

    @Mock
    JmsPropertyScanner.Visitor visitor;
    JmsPropertyScanner scanner;

    @Before
    public void before() {
        scanner = new JmsPropertyScanner(visitor);
    }

    @After
    public void after() {
        verifyNoMoreInteractions(visitor);
    }

    static class None {
        public String none = "value";
    }

    @Test
    public void shouldNotFindUnannotatedProperty() throws Exception {
        scanner.scan(new None());
    }

    static class Simple {
        @JmsProperty
        public String simple = "value";
    }

    @Test
    public void shouldFindSimpleProperty() throws Exception {
        Simple pojo = new Simple();

        scanner.scan(pojo);

        verify(visitor).visit("simple", pojo, Simple.class.getField("simple"));
    }

    static class Private {
        @JmsProperty
        private final String priv = "value";
    }

    @Test
    public void shouldFindPrivateProperty() throws Exception {
        Private pojo = new Private();

        scanner.scan(pojo);

        verify(visitor).visit("priv", pojo, Private.class.getDeclaredField("priv"));
    }

    @Test
    public void shouldMakePrivateFieldAccessibleProperty() throws Exception {
        final String[] value = new String[1];
        Private pojo = new Private();
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Object object = invocation.getArguments()[1];
                Field field = (Field) invocation.getArguments()[2];
                value[0] = (String) field.get(object);
                return null;
            }
        }).when(visitor).visit("priv", pojo, Private.class.getDeclaredField("priv"));

        scanner.scan(pojo);

        verify(visitor).visit("priv", pojo, Private.class.getDeclaredField("priv"));
        assertEquals("value", value[0]);
    }

    @Test
    public void shouldFindSimpleNullProperty() throws Exception {
        Simple pojo = new Simple();
        pojo.simple = null;

        scanner.scan(pojo);

        verify(visitor).visit("simple", pojo, Simple.class.getField("simple"));
    }

    static class Two {
        @JmsProperty
        public String one = "1";
        @JmsProperty
        public String two = "2";
    }

    @Test
    public void shouldFindTwoProperties() throws Exception {
        Two pojo = new Two();

        scanner.scan(pojo);

        verify(visitor).visit("one", pojo, Two.class.getField("one"));
        verify(visitor).visit("two", pojo, Two.class.getField("two"));
    }

    static class SimpleInteger {
        @JmsProperty
        public Integer integer = 123;
    }

    @Test
    public void shouldFindSimpleIntegerProperty() throws Exception {
        SimpleInteger pojo = new SimpleInteger();

        scanner.scan(pojo);

        verify(visitor).visit("integer", pojo, SimpleInteger.class.getField("integer"));
    }

    static class SimpleInt {
        @JmsProperty
        public int integer = 123;
    }

    @Test
    public void shouldFindSimpleIntProperty() throws Exception {
        SimpleInt pojo = new SimpleInt();

        scanner.scan(pojo);

        verify(visitor).visit("integer", pojo, SimpleInt.class.getField("integer"));
    }

    static class ArrayContainer {
        @JmsProperty
        public String[] array = { "one", "two", "three" };
    }

    @Test
    public void shouldFindArrayProperty() throws Exception {
        ArrayContainer pojo = new ArrayContainer();

        scanner.scan(pojo);

        verify(visitor).visit("array", pojo, ArrayContainer.class.getField("array"));
    }

    static class ListContainer {
        @JmsProperty
        public List<String> list = ImmutableList.of("one", "two", "three");
    }

    @Test
    public void shouldFindListProperty() throws Exception {
        ListContainer pojo = new ListContainer();

        scanner.scan(pojo);

        verify(visitor).visit("list", pojo, ListContainer.class.getField("list"));
    }

    static class SetContainer {
        @JmsProperty
        public Set<String> set = ImmutableSet.of("one", "two", "three");
    }

    @Test
    public void shouldFindSetProperty() throws Exception {
        SetContainer pojo = new SetContainer();

        scanner.scan(pojo);

        verify(visitor).visit("set", pojo, SetContainer.class.getField("set"));
    }

    static class MapContainer {
        @JmsProperty
        public Map<String, String> map = ImmutableMap.of("one", "1", "two", "2", "three", "3");
    }

    @Test
    public void shouldFindMapProperty() throws Exception {
        MapContainer pojo = new MapContainer();

        scanner.scan(pojo);

        verify(visitor).visit("map", pojo, MapContainer.class.getField("map"));
    }

    static class NestedNone {
        @JmsProperty
        public None nested = new None();
    }

    @Test
    public void shouldFindNestedNoneProperty() throws Exception {
        NestedNone pojo = new NestedNone();

        scanner.scan(pojo);

        verify(visitor).visit("nested/none", pojo.nested, None.class.getField("none"));
    }

    static class NestedSimple {
        public Simple nested = new Simple();
    }

    @Test
    public void shouldFindNestedSimpleProperty() throws Exception {
        NestedSimple pojo = new NestedSimple();

        scanner.scan(pojo);

        verify(visitor).visit("nested/simple", pojo.nested, Simple.class.getField("simple"));
    }

    static class NestedPrivate {
        private final Simple nested = new Simple();
    }

    @Test
    public void shouldFindNestedPrivateProperty() throws Exception {
        NestedPrivate pojo = new NestedPrivate();

        scanner.scan(pojo);

        verify(visitor).visit("nested/simple", pojo.nested, Simple.class.getField("simple"));
    }

    static class Static {
        public static String staticc = "value";
    }

    @Test
    public void shouldFindStaticProperty() throws Exception {
        Static pojo = new Static();

        scanner.scan(pojo);
    }

    static class NestedStatic {
        @JmsProperty
        public Static nested = new Static();
    }

    @Test
    public void shouldFindNestedStaticProperty() throws Exception {
        NestedStatic pojo = new NestedStatic();

        scanner.scan(pojo);
    }

    static class RecursiveContainer {
        @JmsProperty
        public RecursiveContent content;
    }

    static class RecursiveContent {
        public RecursiveContainer recursion;
        public String body;
    }

    @Test
    public void shouldFindRecursiveProperty() throws Exception {
        RecursiveContainer pojo = new RecursiveContainer();
        pojo.content = new RecursiveContent();
        pojo.content.recursion = pojo;

        scanner.scan(pojo);

        verify(visitor).visit("content/body", pojo.content, RecursiveContent.class.getField("body"));
    }
}
