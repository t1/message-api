package net.java.messageapi.adapter;

import static org.custommonkey.xmlunit.XMLAssert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.*;

import javax.xml.bind.*;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.java.messageapi.converter.*;

import org.junit.*;

public class JmsMappingAdapterTest {

    @XmlRootElement(name = "test-container")
    static class Container {
        @XmlElement
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

    @XmlRootElement
    public static class SimpleType {
        @XmlValue
        public final String value;

        // satisfy JAXB
        @SuppressWarnings("unused")
        private SimpleType() {
            this.value = null;
        }

        public SimpleType(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    @XmlRootElement
    public static class SimpleTypeConverter extends Converter<SimpleType> {
        @Override
        public String marshal(SimpleType v) throws Exception {
            return v.value;
        }

        @Override
        public SimpleType unmarshal(String v) throws Exception {
            return new SimpleType(v);
        }
    }

    private static final String XML = ""//
            + "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" //
            + "<test-container>\n" //
            + "    <mapping methodName=\"method\" upperCase=\"true\">\n"//
            + "        <mapField from=\"s1\" to=\"A\"/>\n"//
            + "        <mapField from=\"s5\" to=\"E\">\n"//
            + "            <stringToBooleanConverter true=\"yes\" false=\"no\"/>\n"//
            + "        </mapField>\n"//
            + "        <mapField from=\"s6\" to=\"F\">\n"//
            + "            <simpleTypeConverter/>\n"//
            + "            <default>\n"//
            + "                <simpleType>FFF</simpleType>\n"//
            + "            </default>\n"//
            + "        </mapField>\n"//
            + "        <mapOperation from=\"m1\">o1</mapOperation>\n"//
            + "        <mapOperation from=\"m2\">o2</mapOperation>\n"//
            + "    </mapping>\n"//
            + "</test-container>\n";

    private static final SimpleType DEFAULT = new SimpleType("FFF");
    private static final Container CONTAINER = new Container();
    static {
        CONTAINER.mapping = new MappingBuilder("method") //
                .mapOperation("m1", "o1") //
                .mapOperation("m2", "o2") //
                .mapField("s1", FieldMapping.map("A")) //
                .mapField("s5", FieldMapping.map("E", new StringToBooleanConverter("yes", "no"))) //
                .mapField("s6", FieldMapping.mapWithDefault("F", new SimpleTypeConverter(), DEFAULT)) //
                .upperCaseFields() //
                .build();
    }

    @Rule
    public XmlUnitRule xml = new XmlUnitRule().ignoreWhitespace();

    @Rule
    public JaxbRule jaxb = new JaxbRule();

    @Test
    public void shouldMarshal() throws Exception {
        StringWriter writer = new StringWriter();
        Marshaller marshaller = jaxb.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(CONTAINER, writer);

        assertXMLEqual(XML, writer.toString().replace(" standalone=\"yes\"", ""));
    }

    @Test
    public void shouldUnmarshal() throws Exception {
        Unmarshaller unmarshaller = jaxb.createUnmarshaller();
        Container container = (Container) unmarshaller.unmarshal(new StringReader(XML));

        assertThat(CONTAINER.mapping.toString(), is(equalTo(container.mapping.toString())));
    }
}
