package net.java.messageapi.adapter.mapped;

class DefaultMapping implements Mapping {

    private final String operationMessageAttribute;

    public DefaultMapping(String operationMessageAttribute) {
        CheckedMapping.check(operationMessageAttribute, "invalid operation field name");
        this.operationMessageAttribute = operationMessageAttribute;
    }

    @Override
    public FieldMapping<?> getMappingForField(String fieldName) {
        return FieldMapping.map(fieldName);
    }

    @Override
    public String getOperationMessageAttibute() {
        return operationMessageAttribute;
    }

    @Override
    public String getMethodForOperation(String operationName) {
        return operationName;
    }

    @Override
    public String getOperationForMethod(String methodName) {
        return methodName;
    }

    @Override
    public String toString() {
        return "Mapping[" + operationMessageAttribute + "]";
    }
}