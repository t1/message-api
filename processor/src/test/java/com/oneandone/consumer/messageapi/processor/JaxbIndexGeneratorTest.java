package com.oneandone.consumer.messageapi.processor;

import static org.junit.Assert.*;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;

import javax.annotation.processing.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.base.Suppliers;
import com.oneandone.consumer.messageapi.processor.mock.FilerDummy;
import com.oneandone.consumer.messageapi.processor.mock.ProcessingEnvironmentDummy;

@RunWith(MockitoJUnitRunner.class)
public class JaxbIndexGeneratorTest {

    @Mock
    private Messager messager;

    private final StringWriter stringWriter = new StringWriter();

    private final Filer filer = new FilerDummy(Suppliers.<Writer> ofInstance(stringWriter));

    private final Processor processor = new XmlRootElementAnnotationProcessor();

    @Test
    public void shouldCreateListOfClassNames() throws Exception {
        generate();
        List<String> jaxbIndexList = readJaxbIndex();
        assertTrue(jaxbIndexList.contains(AnnotationProcessorTestMethod1.class.getSimpleName()));
        assertTrue(jaxbIndexList.contains(AnnotationProcessorTestMethod2.class.getSimpleName()));
        assertTrue(jaxbIndexList.contains(AnnotationProcessorTestMethod3.class.getSimpleName()));
    }

    private void generate() {
        new ProcessingEnvironmentDummy(messager, filer).process(processor,
                AnnotationProcessorTestMethod1.class, AnnotationProcessorTestMethod2.class,
                AnnotationProcessorTestMethod3.class);
    }

    private List<String> readJaxbIndex() {
        String jaxbIndexContents = stringWriter.toString();
        return Arrays.asList(jaxbIndexContents.split("\n"));
    }
}
