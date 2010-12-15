package net.java.messageapi.adapter.mapped;

import com.google.common.collect.ImmutableBiMap;

class MapOperationsMapping extends MappingDecorator {

    private final ImmutableBiMap<String, String> map;

    public MapOperationsMapping(Mapping target, ImmutableBiMap<String, String> map) {
        super(target);
        this.map = map;
    }

    @Override
    public String getMethodForOperation(String operationName) {
        final String value = map.inverse().get(operationName);
        return (value == null) ? super.getMethodForOperation(operationName) : value;
    }

    @Override
    public String getOperationForMethod(String methodName) {
        final String value = map.get(methodName);
        return (value == null) ? super.getOperationForMethod(methodName) : value;
    }

    @Override
    public String toString() {
        return super.toString() + "[operations=" + map + "]";
    }
}