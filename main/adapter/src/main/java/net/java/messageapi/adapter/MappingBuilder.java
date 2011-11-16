package net.java.messageapi.adapter;

import java.lang.reflect.Method;

import net.java.messageapi.JmsMappedName;
import net.java.messageapi.JmsMappedPayload;
import net.java.messageapi.reflection.Parameter;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;

public class MappingBuilder {

    private static final String DEFAULT_OPERATION_FIELD = "METHOD";
    public static final Mapping DEFAULT = new MappingBuilder(DEFAULT_OPERATION_FIELD).build();

    private Mapping mapping;
    private ImmutableMap.Builder<String, FieldMapping<?>> fieldMap;
    private ImmutableBiMap.Builder<String, String> opMap;

    public MappingBuilder(String operationName) {
        this.mapping = new DefaultMapping(operationName);
    }

    public MappingBuilder(Class<?> api) {
        JmsMappedPayload annotation = api.getAnnotation(JmsMappedPayload.class);
        String operationName = (annotation == null) ? DEFAULT_OPERATION_FIELD
                : annotation.operationName();
        this.mapping = new DefaultMapping(operationName);

        if (annotation != null && annotation.upperCaseFields())
            upperCaseFields();

        for (Method method : api.getMethods()) {
            mapMethod(method);
        }
    }

    private void mapMethod(Method method) {
        JmsMappedName mappedOperation = method.getAnnotation(JmsMappedName.class);
        if (mappedOperation != null) {
            String methodName = method.getName();
            String mappedName = mappedOperation.value();
            mapOperation(methodName, mappedName);
        }
        for (Parameter parameter : Parameter.allOf(method)) {
            mapParameter(parameter);
        }
    }

    private void mapParameter(Parameter parameter) {
        JmsMappedName mappedParameter = parameter.getAnnotation(JmsMappedName.class);
        if (mappedParameter != null) {
            mapField(parameter.getName(), mappedParameter.value());
        }
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
