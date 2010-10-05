package com.oneandone.consumer.messageapi.adapter.mapped;

public class CheckedMapping extends MappingDecorator {

    static void check(String value, String message) {
        if (!isValidName(value)) {
            throw new IllegalArgumentException(message + ": " + value);
        }
    }

    private static boolean isValidName(String name) {
        return name != null && name.length() > 0;
    }

    public CheckedMapping(Mapping target) {
        super(target);
    }

    @Override
    public String getOperationMessageAttibute() {
        String operation = super.getOperationMessageAttibute();
        check(operation, "operation attribute name invalid");
        return operation;
    }

    @Override
    public FieldMapping<?> getMappingForField(String fieldName) {
        FieldMapping<?> fieldMapping = super.getMappingForField(fieldName);
        if (fieldMapping == null)
            throw new IllegalArgumentException("no mapping for field: " + fieldName);
        check(fieldMapping.getAttributeName(), "invalid message attribute for field " + fieldName);
        return fieldMapping;
    }

    @Override
    public String getOperationForMethod(String methodName) {
        String operationName = super.getOperationForMethod(methodName);
        check(operationName, "invalid operation name for the method name " + methodName);
        return operationName;
    }

    @Override
    public String getMethodForOperation(String operationName) {
        String methodName = super.getMethodForOperation(operationName);
        check(methodName, "invalid method name for the operation name " + operationName);
        return methodName;
    }

    @Override
    public String toString() {
        return "Checked " + super.toString();
    }
}
