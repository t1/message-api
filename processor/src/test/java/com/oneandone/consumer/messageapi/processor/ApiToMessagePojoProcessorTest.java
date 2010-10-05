package com.oneandone.consumer.messageapi.processor;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.List;

import javax.annotation.Generated;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic.Kind;
import javax.xml.bind.annotation.*;

import net.sf.twip.TwiP;

import org.joda.time.Instant;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.oneandone.consumer.messageapi.MessageApi;
import com.oneandone.consumer.messageapi.processor.mock.*;
import com.oneandone.consumer.messageapi.processor.pojo.Pojo;
import com.oneandone.consumer.tools.collect.iterables.Iterables2;

@RunWith(TwiP.class)
public class ApiToMessagePojoProcessorTest {

    private static final List<String> REQUIRED_IMPORTS = ImmutableList.of(
            Generated.class.getName(), XmlRootElement.class.getName(), XmlType.class.getName(),
            XmlElement.class.getName());

    private static final String PACKAGE = ApiToMessagePojoProcessorTest.class.getPackage().getName();

    @Mock
    private Messager messager;

    private final MessageApiAnnotationProcessor processor = new MessageApiAnnotationProcessor();

    @After
    public void after() {
        assertTrue(processor.getGeneratedPojos().isEmpty());
    }

    private void convert(Class<?> type) {
        new ProcessingEnvironmentDummy(messager).process(processor, type);
    }

    private Pojo popPojo() {
        // verify messages before popping the generated pojo, so we can see any errors
        verify(messager, atLeast(0)).printMessage(eq(Kind.NOTE), anyString());
        verifyNoMoreInteractions(messager);

        return processor.getGeneratedPojos().remove(0);
    }

    private void assertPojoGenerated(String pojoName) {
        Function<Pojo, String> simpleNameConverter = new Function<Pojo, String>() {

            @Override
            public String apply(Pojo from) {
                return from.getSimpleName();
            }
        };
        Pojo generated = Iterables2.findFirst(processor.getGeneratedPojos(), simpleNameConverter,
                Predicates.equalTo(pojoName));
        assertNotNull(generated);
        processor.getGeneratedPojos().remove(generated);
    }

    @MessageApi
    public class ClassApi {
        public void call() {
            throw new UnsupportedOperationException();
        }
    }

    @Test
    public void shouldNotAcceptAnnotatedClass() throws Exception {
        convert(ClassApi.class);

        verify(messager).printMessage(Kind.ERROR,
                "The MessageApi annotation can only be put on an interface, not on a CLASS",
                new TypeElementImpl(ClassApi.class));
        verify(messager).printMessage(Kind.ERROR,
                "can't process MessageApi: " + UnsupportedOperationException.class.getName(),
                new TypeElementImpl(ClassApi.class));
    }

    @MessageApi
    public interface EmptyApi {
        // no methods
    }

    @Test
    public void shouldNotProcessEmptyApi() throws Exception {
        convert(EmptyApi.class);

        verify(messager).printMessage(Kind.ERROR, "MessageApi must have methods",
                new TypeElementImpl(EmptyApi.class));
    }

    @MessageApi
    public interface ReturnsApi {
        public String returningCall();
    }

    @Test
    public void shouldNotAcceptReturningMethod() throws Exception {
        convert(ReturnsApi.class);

        verify(messager).printMessage(Kind.NOTE,
                "Processing MessageApi [" + ReturnsApi.class.getName() + "]");
        verify(messager).printMessage(Kind.NOTE,
                "Writing .parametermap for " + ReturnsApi.class.getName());
        verify(messager).printMessage(Kind.ERROR,
                "MessageApi methods must return void; they are asynchronous!",
                new MethodElementImpl(ReturnsApi.class.getMethod("returningCall")));
        verifyNoMoreInteractions(messager);
    }

    @MessageApi
    public interface ThrowingApi {
        public void throwingCall() throws RuntimeException;
    }

    @Test
    // (expected = MessageApiThrowingMethodException.class)
    public void shouldNotAcceptThrowingMethod() throws Exception {
        convert(ThrowingApi.class);

        verify(messager).printMessage(Kind.ERROR,
                "MessageApi methods must not declare an exception; they are asynchronous!",
                new MethodElementImpl(ThrowingApi.class.getMethod("throwingCall")));
    }

    @MessageApi
    public interface NoArgApi {
        public void noArgCall();
    }

    @Test
    public void noArgPojoShouldStartWithPackageDeclaration() throws Exception {
        convert(NoArgApi.class);

        assertEquals(PACKAGE, popPojo().getPackage());
    }

    @Test
    public void noArgPojoShouldImportOnlyRequiredClasses() throws Exception {
        convert(NoArgApi.class);

        final List<String> expected = ImmutableList.of(Generated.class.getName(),
                XmlRootElement.class.getName(), XmlType.class.getName());
        assertEquals(expected, popPojo().getImports());
    }

    @Test
    public void noArgPojoShouldHaveXmlAnnotation() {
        convert(NoArgApi.class);

        assertNotNull(popPojo().getAnnotation(XmlRootElement.class));
    }

    @Test
    public void processNoArg() throws Exception {
        convert(NoArgApi.class);

        Pojo pojo = popPojo();
        assertEquals("NoArgCall", pojo.getSimpleName());
        assertEquals(0, pojo.getProperties().size());
    }

    @MessageApi
    public interface StringApi {
        public void stringCall(String value);
    }

    @Test
    public void processString() throws Exception {
        convert(StringApi.class);

        Pojo pojo = popPojo();
        assertEquals("StringCall", pojo.getSimpleName());
        assertEquals(1, pojo.getProperties().size());
        assertEquals(String.class.getName(), pojo.getProperty("arg0").getType());
    }

    @Test
    public void stringPojoShouldImportOnlyRequiredClasses() throws Exception {
        convert(StringApi.class);

        assertEquals(REQUIRED_IMPORTS, popPojo().getImports());
    }

    @MessageApi
    public interface IntApi {
        public void intCall(int value);
    }

    @Test
    public void processInt() throws Exception {
        convert(IntApi.class);

        Pojo pojo = popPojo();
        assertEquals("IntCall", pojo.getSimpleName());
        assertEquals(1, pojo.getProperties().size());
        assertEquals(Integer.TYPE.getName(), pojo.getProperty("arg0").getType());
    }

    @MessageApi
    public interface InstantApi {
        public void instantCall(Instant value);
    }

    @Test
    public void processInstant() throws Exception {
        convert(InstantApi.class);

        Pojo pojo = popPojo();
        assertEquals("InstantCall", pojo.getSimpleName());
        assertEquals(1, pojo.getProperties().size());
        assertEquals(Instant.class.getName(), pojo.getProperty("arg0").getType());
    }

    @Test
    public void instantPojoShouldImportInstant() throws Exception {
        convert(InstantApi.class);

        ImmutableList<String> expected = ImmutableList.<String> builder().addAll(REQUIRED_IMPORTS).add(
                Instant.class.getName()).build();
        assertEquals(expected, popPojo().getImports());
    }

    @MessageApi
    public interface StringAndInstantApi {
        public void stringAndInstantCall(String value0, Instant value1);
    }

    @Test
    public void processStringAndInstant() throws Exception {
        convert(StringAndInstantApi.class);

        Pojo pojo = popPojo();
        assertEquals("StringAndInstantCall", pojo.getSimpleName());
        assertEquals(2, pojo.getProperties().size());
        assertEquals(String.class.getName(), pojo.getProperty("arg0").getType());
        assertEquals(Instant.class.getName(), pojo.getProperty("arg1").getType());
    }

    @MessageApi
    public interface ArrayApi {
        public void arrayCall(String[] values);
    }

    @Test
    public void processArray() throws Exception {
        convert(ArrayApi.class);

        Pojo pojo = popPojo();
        assertEquals("ArrayCall", pojo.getSimpleName());
        assertEquals(1, pojo.getProperties().size());
        assertEquals(String.class.getName() + "[]", pojo.getProperty("arg0").getType());
    }

    @MessageApi
    public interface VarargApi {
        public void varargCall(String... values);
    }

    @Test
    public void processVarargs() throws Exception {
        convert(VarargApi.class);

        Pojo pojo = popPojo();
        assertEquals("VarargCall", pojo.getSimpleName());
        assertEquals(1, pojo.getProperties().size());
        assertEquals(String.class.getName() + "[]", pojo.getProperty("arg0").getType());
    }

    @MessageApi
    public interface TwoMethodNamesApi {
        public void methodA();

        public void methodB();
    }

    @Test
    public void shouldAcceptTwoMethodNames() throws Exception {
        convert(TwoMethodNamesApi.class);

        assertPojoGenerated("MethodA");
        assertPojoGenerated("MethodB");
    }

    @MessageApi
    public interface AmbiguousMethodNamesApi {
        public void method(String a);

        public void method(Integer b);

        public void method(Instant c);
    }

    @Test
    public void shouldAcceptAmbiguousMethodName() throws Exception {
        convert(AmbiguousMethodNamesApi.class);

        verify(messager, times(3)).printMessage(eq(Kind.WARNING), anyString(),
                (Element) anyObject());

        assertEquals("MethodString", popPojo().getSimpleName());
        assertEquals("MethodInteger", popPojo().getSimpleName());
        assertEquals("MethodOrgJodaTimeInstant", popPojo().getSimpleName());
    }

    @Test
    public void shouldWarnAmbiguousMethodName() throws Exception {
        convert(AmbiguousMethodNamesApi.class);

        processor.getGeneratedPojos().clear();

        verifyAmbiguousMethodName(String.class);
        verifyAmbiguousMethodName(Integer.class);
        verifyAmbiguousMethodName(Instant.class);
    }

    private void verifyAmbiguousMethodName(Class<?>... args) throws NoSuchMethodException {
        verify(messager).printMessage(
                Kind.WARNING,
                "ambiguous method name; it's generally better to use unique names "
                        + "and not rely on the parameter type mangling.",
                new MethodElementImpl(AmbiguousMethodNamesApi.class.getMethod("method", args)));
    }
}
