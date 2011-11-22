package net.java.messageapi.processor;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.Serializable;
import java.util.*;

import javax.annotation.Generated;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic.Kind;
import javax.xml.bind.annotation.*;

import net.java.messageapi.*;
import net.java.messageapi.pojo.Pojo;
import net.java.messageapi.pojo.PojoProperty;
import net.java.messageapi.processor.mock.*;
import net.sf.twip.TwiP;

import org.joda.time.Instant;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

@RunWith(TwiP.class)
public class ApiToMessagePojoProcessorTest {

    private static final Set<String> REQUIRED_IMPORTS = ImmutableSet.of( //
            Generated.class.getName(), //
            XmlRootElement.class.getName(), //
            XmlType.class.getName(), //
            XmlElement.class.getName(), //
            Serializable.class.getName());

    private static final String PACKAGE = ApiToMessagePojoProcessorTest.class.getPackage().getName();

    @Mock
    private Messager messager;

    private final MessageApiAnnotationProcessor processor = new MessageApiAnnotationProcessor();

    @After
    public void after() {
        assertTrue(getGeneratedPojos().isEmpty());
    }

    private List<Pojo> getGeneratedPojos() {
        return processor.getPojoGenerator().getGeneratedPojos();
    }

    private void convert(Class<?> type) {
        new ProcessingEnvironmentDummy(messager).process(processor, type);
    }

    private Pojo popPojo() {
        // verify messages before popping the generated pojo, so we can see any errors
        verify(messager, atLeast(0)).printMessage(eq(Kind.NOTE), anyString());
        verifyNoMoreInteractions(messager);

        return getGeneratedPojos().remove(0);
    }

    private void assertPojoGenerated(final String pojoName) {
        Pojo generated = Iterables.find(getGeneratedPojos(), new Predicate<Pojo>() {
            @Override
            public boolean apply(Pojo input) {
                return input.getSimplifiedClassName().equals(pojoName);
            }
        });
        assertNotNull(generated);
        getGeneratedPojos().remove(generated);
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

        verify(messager).printMessage(Kind.ERROR, "MessageApi must have methods", new TypeElementImpl(EmptyApi.class));
    }

    @MessageApi
    public interface ReturnsApi {
        public String returningCall();
    }

    @Test
    public void shouldNotAcceptReturningMethod() throws Exception {
        convert(ReturnsApi.class);

        verify(messager).printMessage(Kind.NOTE, "Processing MessageApi [" + ReturnsApi.class.getName() + "]");
        verify(messager).printMessage(Kind.NOTE, "Writing .parametermap for " + ReturnsApi.class.getName());
        verify(messager).printMessage(Kind.ERROR, "MessageApi methods must return void; they are asynchronous!",
                new MethodElementImpl(ReturnsApi.class.getMethod("returningCall")));
        verifyNoMoreInteractions(messager);
    }

    @MessageApi
    public interface ThrowingApi {
        public void throwingCall() throws RuntimeException;
    }

    @Test
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

        Set<String> expected = ImmutableSet.of(Generated.class.getName(), XmlRootElement.class.getName(),
                XmlType.class.getName(), Serializable.class.getName());
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
        assertEquals(name(NoArgApi.class) + "$NoArgCall", pojo.getSimplifiedClassName());
        assertEquals(0, pojo.getProperties().size());
    }

    /**
     * Extract the name of the class without the package prefix. This is not the simple name if it is a nested class.
     */
    private String name(Class<?> type) {
        String pkg = type.getPackage().getName();
        String name = type.getName();
        assertTrue(name.startsWith(pkg + "."));
        name = name.substring(pkg.length() + 1);
        return name;
    }

    @MessageApi
    public interface StringApi {
        public void stringCall(String value);
    }

    @Test
    public void processString() throws Exception {
        convert(StringApi.class);

        Pojo pojo = popPojo();
        assertEquals(name(StringApi.class) + "$StringCall", pojo.getSimplifiedClassName());
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
        assertEquals(name(IntApi.class) + "$IntCall", pojo.getSimplifiedClassName());
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
        assertEquals(name(InstantApi.class) + "$InstantCall", pojo.getSimplifiedClassName());
        assertEquals(Instant.class.getName(), pojo.getProperty("arg0").getType());
    }

    @Test
    public void instantPojoShouldImportInstant() throws Exception {
        convert(InstantApi.class);

        ImmutableSet<String> expected = ImmutableSet.<String> builder().addAll(REQUIRED_IMPORTS).add(
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
        assertEquals(name(StringAndInstantApi.class) + "$StringAndInstantCall", pojo.getSimplifiedClassName());
        assertEquals(2, pojo.getProperties().size());
        assertEquals(String.class.getName(), pojo.getProperty("arg0").getType());
        assertEquals(Instant.class.getName(), pojo.getProperty("arg1").getType());
    }

    @Test
    public void shouldProducePropOrder() throws Exception {
        convert(StringAndInstantApi.class);

        Map<String, Object> xmlType = popPojo().getAnnotation(XmlType.class);
        String[] propOrder = (String[]) xmlType.get("propOrder");

        assertEquals(2, propOrder.length);
        assertEquals("arg0", propOrder[0]);
        assertEquals("arg1", propOrder[1]);
    }

    @MessageApi
    public interface ArrayApi {
        public void arrayCall(String[] values);
    }

    @Test
    public void processArray() throws Exception {
        convert(ArrayApi.class);

        Pojo pojo = popPojo();
        assertEquals(name(ArrayApi.class) + "$ArrayCall", pojo.getSimplifiedClassName());
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
        assertEquals(name(VarargApi.class) + "$VarargCall", pojo.getSimplifiedClassName());
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

        String prefix = name(TwoMethodNamesApi.class) + "$";
        assertPojoGenerated(prefix + "MethodA");
        assertPojoGenerated(prefix + "MethodB");
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

        verify(messager, times(3)).printMessage(eq(Kind.WARNING), anyString(), (Element) anyObject());

        String prefix = name(AmbiguousMethodNamesApi.class) + "$";
        assertEquals(prefix + "MethodString", popPojo().getSimplifiedClassName());
        assertEquals(prefix + "MethodInteger", popPojo().getSimplifiedClassName());
        assertEquals(prefix + "MethodOrgJodaTimeInstant", popPojo().getSimplifiedClassName());
    }

    @Test
    public void shouldWarnAmbiguousMethodName() throws Exception {
        convert(AmbiguousMethodNamesApi.class);

        getGeneratedPojos().clear();

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

    @MessageApi
    public interface OptionalArgumentApi {
        public void optionalArgument(@Optional String arg0);
    }

    @Test
    public void shouldRecognizeOptionalArgument() throws Exception {
        convert(OptionalArgumentApi.class);

        PojoProperty property = popPojo().getProperty("arg0");
        Map<String, Object> xmlElementFields = property.getAnnotationFieldsFor(XmlElement.class);
        assertEquals(false, xmlElementFields.get("required"));
    }

    @Test
    public void shouldKeepOptionalArgumentInPropOrder() throws Exception {
        convert(OptionalArgumentApi.class);

        Map<String, Object> xmlType = popPojo().getAnnotation(XmlType.class);
        String[] propOrder = (String[]) xmlType.get("propOrder");

        assertEquals(1, propOrder.length);
        assertEquals("arg0", propOrder[0]);
    }

    @MessageApi
    public interface JmsPropertyApi {
        public void annotatedMethod(@JmsProperty String arg0);
    }

    @Test
    public void shouldFindSingleArgument() throws Exception {
        convert(JmsPropertyApi.class);

        Pojo pojo = popPojo();
        assertEquals(1, pojo.getProperties().size());
    }

    @Test
    public void localTypeShouldBeString() throws Exception {
        convert(JmsPropertyApi.class);

        PojoProperty property = popPojo().getProperty("arg0");
        assertEquals("String", property.getLocalType());
    }

    @Test
    public void shouldReadFallbackArgumentName() throws Exception {
        convert(JmsPropertyApi.class);

        PojoProperty property = popPojo().getProperty("arg0");
        assertEquals("arg0", property.getName());
    }

    @Test
    public void shouldAddJmsPropertyAnnotationToPojo() throws Exception {
        convert(JmsPropertyApi.class);

        PojoProperty property = popPojo().getProperty("arg0");

        assertTrue(property.isAnnotatedAs(JmsProperty.class));
    }

    @Test
    public void shouldAnnotateXmlElement() throws Exception {
        convert(StringApi.class);

        PojoProperty property = popPojo().getProperty("arg0");

        Map<String, Object> xmlElementFields = property.getAnnotationFieldsFor(XmlElement.class);
        assertEquals(1, xmlElementFields.size());
        assertEquals(true, xmlElementFields.get("required"));
    }

    @Test
    public void shouldKeepPropOrder() throws Exception {
        convert(StringApi.class);

        Map<String, Object> xmlType = popPojo().getAnnotation(XmlType.class);
        String[] propOrder = (String[]) xmlType.get("propOrder");

        assertEquals(1, propOrder.length);
        assertEquals("arg0", propOrder[0]);
    }

    @Test
    public void shouldMakeJmsPropertyTransient() throws Exception {
        convert(JmsPropertyApi.class);

        PojoProperty property = popPojo().getProperty("arg0");

        assertTrue(property.isTransient());
    }

    @Test
    public void shouldSkipPropOrderForXmlTransient() throws Exception {
        convert(JmsPropertyApi.class);

        Map<String, Object> xmlType = popPojo().getAnnotation(XmlType.class);
        String[] propOrder = (String[]) xmlType.get("propOrder");

        assertEquals(0, propOrder.length);
    }
}
