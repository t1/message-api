package net.java.messageapi.adapter.mapped;

import static org.junit.Assert.*;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.java.messageapi.converter.*;

import org.junit.Test;

public class JmsMappingAdapterTest {

    @XmlRootElement
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
            + "        <mapField to=\"A\" from=\"s1\"/>\n"//
            + "        <mapField to=\"B\" from=\"s2\">\n"//
            + "            <jodaInstantConverter/>\n"//
            + "        </mapField>\n"//
            + "        <mapField to=\"C\" from=\"s3\">\n"//
            + "            <stringToBooleanConverter false=\"no\" true=\"yes\"/>\n"//
            + "        </mapField>\n"//
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
        .mapField("s2", FieldMapping.map("B", new JodaInstantConverter())) //
        .mapField("s3", FieldMapping.map("C", new StringToBooleanConverter("yes", "no"))) //
        .upperCaseFields() //
        .build();
    }

    private final JAXBContext context;

    public JmsMappingAdapterTest() throws Exception {
        this.context = JAXBContext.newInstance(Container.class, Converter.class);
    }

    @Test
    public void shouldMarshal() throws Exception {
        StringWriter writer = new StringWriter();
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(CONTAINER, writer);

        assertEquals(XML, writer.toString());
    }

    @Test
    public void shouldUnmarshal() throws Exception {
        Unmarshaller unmarshaller = context.createUnmarshaller();
        Container container = (Container) unmarshaller.unmarshal(new StringReader(XML));

        assertEquals(CONTAINER.mapping.toString(), container.mapping.toString());
    }
}
