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
            + "    <mapping methodName=\"method\">\n"//
            + "        <map from=\"s1\">A</map>\n"//
            + "        <map from=\"s2\">B</map>\n"//
            + "    </mapping>\n"//
            + "</container>\n";

    private static final Container CONTAINER = new Container();
    static {
        CONTAINER.mapping = new MappingBuilder("method") //
        .mapField("s1", FieldMapping.map("A")) //
        .mapField("s2", FieldMapping.map("B")) //
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
