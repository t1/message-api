package net.java.messageapi.adapter;

class MappingDecorator implements Mapping {

    final Mapping target;

    public MappingDecorator(Mapping target) {
        if (target == null)
            throw new NullPointerException("target mapping must not be null");
        this.target = target;
    }

    @Override
    public String getOperationMessageAttibute() {
        return target.getOperationMessageAttibute();
    }

    @Override
    public FieldMapping<?> getMappingForField(String fieldName) {
        return target.getMappingForField(fieldName);
    }

    @Override
    public String getOperationForMethod(String methodName) {
        return target.getOperationForMethod(methodName);
    }

    @Override
    public String getMethodForOperation(String operationName) {
        return target.getMethodForOperation(operationName);
    }

    @Override
    public String toString() {
        return target.toString();
    }
}
