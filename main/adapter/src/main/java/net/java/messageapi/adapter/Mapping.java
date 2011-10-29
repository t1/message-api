package net.java.messageapi.adapter;

/**
 * Use this class to change the names of the fields in a mapped message.
 */
public interface Mapping {

    /**
     * The mapped message field name for that pojo property
     */
    public FieldMapping<?> getMappingForField(String fieldName);

    /**
     * The mapped message field name for the operation name
     */
    public String getOperationMessageAttibute();

    /**
     * The name of the operation for that method name; will be stored into the mapped message
     */
    public String getOperationForMethod(String methodName);

    /**
     * The method name for that operation name coming from the mapped message
     */
    public String getMethodForOperation(String operationName);
}
