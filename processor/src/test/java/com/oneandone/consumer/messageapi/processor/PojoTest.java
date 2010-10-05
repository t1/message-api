package com.oneandone.consumer.messageapi.processor;

import static org.junit.Assert.*;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;

import net.sf.twip.TwiP;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.Lists;
import com.oneandone.consumer.messageapi.processor.pojo.Pojo;
import com.oneandone.consumer.messageapi.processor.pojo.PojoProperty;

@RunWith(TwiP.class)
public class PojoTest {

    private String getOtherImports(Pojo pojo) {
        ArrayList<String> imports = Lists.newArrayList(pojo.getImports());
        imports.remove(XmlElement.class.getName());
        return imports.toString();
    }

    @Test
    public void noPropPojo() throws Exception {
        Pojo pojo = new Pojo("test.pkg", "TestClass");

        assertEquals("test.pkg.TestClass", pojo.getName());
        assertEquals("[]", getOtherImports(pojo));
    }

    @Test
    public void nestedClassProperty() throws Exception {
        Pojo pojo = new Pojo("test.pkg", "TestClass");

        pojo.addProperty("some.pack.age.with.a.TypeAnd.NestedType", "prop", true);

        PojoProperty property = pojo.getProperty("prop");
        assertEquals("some.pack.age.with.a.TypeAnd.NestedType", property.getType());
        assertEquals("TypeAnd.NestedType", property.getRawType());
        assertEquals("[some.pack.age.with.a.TypeAnd.NestedType]", getOtherImports(pojo));
    }

    @Test
    public void genericProperty() throws Exception {
        Pojo pojo = new Pojo("test.pkg", "TestClass");

        pojo.addProperty("javax.util.List<java.lang.String>", "list", true);

        PojoProperty property = pojo.getProperty("list");
        assertEquals("javax.util.List<java.lang.String>", property.getType());
        assertEquals("List", property.getRawType());
        assertEquals("List<String>", property.getLocalType());
        assertEquals("[javax.util.List]", getOtherImports(pojo));
    }

    @Test
    public void genericImportProperty() throws Exception {
        Pojo pojo = new Pojo("test.pkg", "TestClass");

        pojo.addProperty("javax.util.List<org.joda.time.Instant>", "list", true);

        PojoProperty property = pojo.getProperty("list");
        assertEquals("javax.util.List<org.joda.time.Instant>", property.getType());
        assertEquals("List", property.getRawType());
        assertEquals("List<Instant>", property.getLocalType());
        assertEquals("[javax.util.List, org.joda.time.Instant]", getOtherImports(pojo));
    }

    @Test
    public void genericPairProperty() throws Exception {
        Pojo pojo = new Pojo("test.pkg", "TestClass");

        pojo.addProperty("javax.util.Map<java.lang.Integer, java.lang.String>", "list", true);

        PojoProperty property = pojo.getProperty("list");
        assertEquals("javax.util.Map<java.lang.Integer, java.lang.String>", property.getType());
        assertEquals("Map", property.getRawType());
        assertEquals("Map<Integer, String>", property.getLocalType());
        assertEquals("[javax.util.Map]", getOtherImports(pojo));
    }

    @Test
    public void nestedGenericProperty() throws Exception {
        Pojo pojo = new Pojo("test.pkg", "TestClass");

        pojo.addProperty("javax.util.List<javax.util.Set<org.joda.time.Instant>>", "list", true);

        PojoProperty property = pojo.getProperty("list");
        assertEquals("javax.util.List<javax.util.Set<org.joda.time.Instant>>", property.getType());
        assertEquals("List", property.getRawType());
        assertEquals("List<Set<Instant>>", property.getLocalType());
        assertEquals("[javax.util.List, javax.util.Set, org.joda.time.Instant]",
                getOtherImports(pojo));
    }

    @Test
    public void primitiveProperty() throws Exception {
        Pojo pojo = new Pojo("test.pkg", "TestClass");

        pojo.addProperty("int", "prop", true);

        PojoProperty property = pojo.getProperty("prop");
        assertEquals("int", property.getType());
        assertEquals("int", property.getRawType());
        assertEquals("[]", getOtherImports(pojo));
    }

    @Test
    public void arrayProperty() throws Exception {
        Pojo pojo = new Pojo("test.pkg", "TestClass");

        pojo.addProperty("int[]", "prop", true);

        PojoProperty property = pojo.getProperty("prop");
        assertEquals("int[]", property.getType());
        assertEquals("int", property.getRawType());
        assertEquals("[]", getOtherImports(pojo));
    }
}
