package net.java.messageapi.adapter.mapped;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;

public class MappingBuilder {

    public static final Mapping DEFAULT = new MappingBuilder("METHOD").build();

    private Mapping mapping;
    private ImmutableMap.Builder<String, FieldMapping<?>> fieldMap;
    private ImmutableBiMap.Builder<String, String> opMap;

    public MappingBuilder(String operationName) {
        this.mapping = new DefaultMapping(operationName);
    }

    public MappingBuilder upperCaseFields() {
        mapping = new UpperCaseFieldNames(mapping);
        return this;
    }

    public MappingBuilder mapField(String property, String attributeName) {
        return mapField(property, FieldMapping.map(attributeName));
    }

    public MappingBuilder mapField(String property, FieldMapping<?> attribute) {
        if (fieldMap == null)
            fieldMap = ImmutableMap.builder();
        fieldMap.put(property, attribute);
        return this;
    }

    public MappingBuilder mapOperation(String method, String operation) {
        if (opMap == null)
            opMap = ImmutableBiMap.builder();
        opMap.put(method, operation);
        return this;
    }

    public Mapping build() {
        Mapping result = mapping;
        if (fieldMap != null)
            result = new MapFieldsMapping(result, fieldMap.build());
        if (opMap != null)
            result = new MapOperationsMapping(result, opMap.build());
        return new CheckedMapping(result);
    }
}
