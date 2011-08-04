package net.java.messageapi.adapter;

import static org.junit.Assert.*;

import java.lang.annotation.Annotation;

import javax.annotation.Resource;

import net.java.messageapi.MessageApiSendDelegate;

import org.joda.time.Instant;
import org.junit.Test;

public class MessageApiSendHandlerTest {

    public class ClassWithMultipleFields {
        String str;

        @Resource(name = "glub")
        Instant inst;

        MessageApiSendDelegate del;
    }

    @Test
    public void shouldFindField() throws Exception {
        MessageApiSendHandler handler = new MessageApiSendHandler();

        Annotation[] annotations = handler.getAnnotations(ClassWithMultipleFields.class,
                ClassWithMultipleFields.class, Instant.class);

        assertEquals(1, annotations.length);
        assertEquals(Resource.class, annotations[0].annotationType());
        Resource r = (Resource) annotations[0];
        assertEquals("glub", r.name());
    }

    public class Inherited extends ClassWithMultipleFields {
        MessageApiSendDelegate dul;
    }

    @Test
    public void shouldFindInheritedField() throws Exception {
        MessageApiSendHandler handler = new MessageApiSendHandler();

        Annotation[] annotations = handler.getAnnotations(Inherited.class, Inherited.class,
                Instant.class);

        assertEquals(1, annotations.length);
        assertEquals(Resource.class, annotations[0].annotationType());
        Resource r = (Resource) annotations[0];
        assertEquals("glub", r.name());
    }
}
