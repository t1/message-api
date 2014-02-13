package net.java.messageapi.adapter;

import static org.junit.Assert.*;

import org.junit.Test;

public class MappingBuilderTest {

    private final MappingBuilder builder = new MappingBuilder("myop");

    private Mapping build() {
        Mapping mapping = builder.build();

        assertEquals("myop", mapping.getOperationMessageAttibute());

        return mapping;
    }

    @Test
    public void shouldBuildMappingForOperation() throws Exception {
        Mapping mapping = build();

        assertEquals("some", mapping.getMappingForField("some").getAttributeName());
        assertEquals("operation", mapping.getMethodForOperation("operation"));
        assertEquals("operation", mapping.getOperationForMethod("operation"));
    }

    @Test
    public void shouldBuildToUpperFieldMapping() throws Exception {
        builder.upperCaseFields();

        Mapping mapping = build();

        assertEquals("myop", mapping.getOperationMessageAttibute());
        assertEquals("LOWER_CASE", mapping.getMappingForField("lowerCase").getAttributeName());
    }

    @Test
    public void shouldBuildMappingForField() throws Exception {
        builder.mapField("property", "field");

        Mapping mapping = build();

        assertEquals("myop", mapping.getOperationMessageAttibute());
        assertEquals("field", mapping.getMappingForField("property").getAttributeName());
    }

    @Test
    public void shouldBuildMappingForFieldAndUpperTheRest() throws Exception {
        builder.mapField("property", "fielD").upperCaseFields();

        Mapping mapping = build();

        assertEquals("myop", mapping.getOperationMessageAttibute());
        assertEquals("fielD", mapping.getMappingForField("property").getAttributeName());
        assertEquals("OTHER", mapping.getMappingForField("other").getAttributeName());
    }

    @Test
    public void shouldBuildOperationMapping() throws Exception {
        builder.mapOperation("method", "operation");

        Mapping mapping = build();

        assertEquals("method", mapping.getMethodForOperation("operation"));
        assertEquals("operation", mapping.getOperationForMethod("method"));

        assertEquals("other", mapping.getMethodForOperation("other"));
        assertEquals("other", mapping.getOperationForMethod("other"));
    }
}
