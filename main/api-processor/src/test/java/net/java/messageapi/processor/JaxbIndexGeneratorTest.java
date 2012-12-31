package net.java.messageapi.processor;

import static org.junit.Assert.*;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;

import net.java.messageapi.processor.mock.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.base.Suppliers;

@RunWith(MockitoJUnitRunner.class)
public class JaxbIndexGeneratorTest {

    @Mock
    private Messager messager;

    private final StringWriter stringWriter = new StringWriter();

    private final Filer filer = new FilerDummy(Suppliers.<Writer> ofInstance(stringWriter));

    private JaxbIndexGenerator generator;

    @Before
    public void before() {
        this.generator = new JaxbIndexGenerator(messager, filer, new ElementUtilDummy());
    }

    @Test
    public void shouldCreateListOfClassNames() throws Exception {
        generate();

        List<String> jaxbIndexList = readJaxbIndex();
        assertTrue(jaxbIndexList.contains(AnnotationProcessorTestMethod1.class.getSimpleName()));
        assertTrue(jaxbIndexList.contains(AnnotationProcessorTestMethod2.class.getSimpleName()));
        assertTrue(jaxbIndexList.contains(AnnotationProcessorTestMethod3.class.getSimpleName()));
    }

    private void generate() {
        generator.process(new TypeElementImpl(AnnotationProcessorTestMethod1.class));
        generator.process(new TypeElementImpl(AnnotationProcessorTestMethod2.class));
        generator.process(new TypeElementImpl(AnnotationProcessorTestMethod3.class));
        generator.finish();
    }

    private List<String> readJaxbIndex() {
        String jaxbIndexContents = stringWriter.toString();
        return Arrays.asList(jaxbIndexContents.split("\n"));
    }
}
