package net.java.messageapi.adapter.mapped;

import static org.junit.Assert.*;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.junit.Test;

public class JmsMappingAdapterTest {
    private static class Container {
        @XmlJavaTypeAdapter(JmsMappingAdapter.class)
        Mapping mapping;

        @Override
        public int hashCode() {
            return mapping.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            Container that = (Container) obj;
            return this.mapping.equals(that.mapping);
        }
    }

    private static final String XML = ""//
            + "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" + "<container>\n" //
            + "    <mapping upperCase=\"true\" methodName=\"method\">\n"//
            + "        <mapField from=\"s1\">A</mapField>\n"//
            + "        <mapField from=\"s2\">B</mapField>\n"//
            + "        <mapOperation from=\"m1\">o1</mapOperation>\n"//
            + "        <mapOperation from=\"m2\">o2</mapOperation>\n"//
            + "    </mapping>\n"//
            + "</container>\n";

    private static final Container CONTAINER = new Container();
    static {
        CONTAINER.mapping = new MappingBuilder("method") //
        .mapOperation("m1", "o1") //
        .mapOperation("m2", "o2") //
        .mapField("s1", FieldMapping.map("A")) //
        .mapField("s2", FieldMapping.map("B")) //
        .upperCaseFields() //
        .build();
    }

    @Test
    public void shouldMarshal() throws Exception {
        StringWriter writer = new StringWriter();
        JAXB.marshal(CONTAINER, writer);

        assertEquals(XML, writer.toString());
    }

    @Test
    public void shouldUnmarshal() throws Exception {
        Container container = JAXB.unmarshal(new StringReader(XML), Container.class);

        assertEquals(CONTAINER.mapping.toString(), container.mapping.toString());
    }
}
