package net.java.messageapi.adapter;

import static junit.framework.Assert.*;
import static org.custommonkey.xmlunit.XMLAssert.*;

import java.io.*;

import javax.xml.bind.*;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.java.messageapi.adapter.JaxbProvider.JaxbProviderMemento;
import net.java.messageapi.converter.*;
import net.sf.twip.*;
import net.sf.twip.Assume;

import org.custommonkey.xmlunit.XMLUnit;
import org.junit.*;
import org.junit.runner.RunWith;

@RunWith(TwiP.class)
public class JmsMappingAdapterTest {

    @XmlRootElement(name = "test-container")
    private static class Container {
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
            + "        <mapField from=\"s2\" to=\"B\">\n"//
            + "            <jodaInstantConverter/>\n"//
            + "        </mapField>\n"//
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
            + "        <mapField from=\"s3\" to=\"C\">\n"//
            + "            <jodaLocalDateConverter/>\n"//
            + "        </mapField>\n"//
            + "        <mapField from=\"s4\" to=\"D\">\n"//
            + "            <jodaLocalDateConverter pattern=\"yyyy-MM-dd\"/>\n"//
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
                .mapField("s2", FieldMapping.map("B", new JodaInstantConverter())) //
                .mapField("s3", FieldMapping.map("C", new JodaLocalDateConverter())) //
                .mapField("s4", FieldMapping.map("D", new JodaLocalDateConverter("yyyy-MM-dd"))) //
                .mapField("s5", FieldMapping.map("E", new StringToBooleanConverter("yes", "no"))) //
                .mapField("s6", FieldMapping.mapWithDefault("F", new SimpleTypeConverter(), DEFAULT)) //
                .upperCaseFields() //
                .build();
    }

    private final JaxbProviderMemento memento; // TODO move this into a JUnit-Rule
    private final JAXBContext context;

    public JmsMappingAdapterTest(@NotNull @Assume("!= XSTREAM") JaxbProvider jaxbProvider) throws Exception {
        this.memento = jaxbProvider.setUp();
        this.context =
                JAXBContext.newInstance(Container.class, Converter.class, SimpleTypeConverter.class, SimpleType.class);
    }

    @After
    public void after() {
        memento.restore();
    }

    @Test
    public void shouldMarshal() throws Exception {
        StringWriter writer = new StringWriter();
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(CONTAINER, writer);

        XMLUnit.setIgnoreWhitespace(true); // TODO move this into a JUnit-Rule
        assertXMLEqual(XML, writer.toString().replace(" standalone=\"yes\"", ""));
    }

    @Test
    public void shouldUnmarshal() throws Exception {
        Unmarshaller unmarshaller = context.createUnmarshaller();
        Container container = (Container) unmarshaller.unmarshal(new StringReader(XML));

        assertEquals(CONTAINER.mapping.toString(), container.mapping.toString());
    }
}
