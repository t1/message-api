package net.java.messageapi.adapter;

import java.util.Map;

class MapOperationsMapping extends MappingDecorator {

    final Map<String, String> map;

    public MapOperationsMapping(Mapping target, Map<String, String> map) {
        super(target);
        this.map = map;
    }

    @Override
    public String getMethodForOperation(String operationName) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (operationName.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return super.getMethodForOperation(operationName);
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