package net.java.messageapi.processor.pojo;

import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

import net.java.messageapi.reflection.TypeMatcher;

import com.google.common.annotations.VisibleForTesting;

/**
 * @see Pojo
 */
@VisibleForTesting
public abstract class PojoProperty {

    public static PojoProperty create(String type, String name) {
        if (type.endsWith("[]"))
            return new ArrayPojoProperty(type, name);
        if ("boolean".equals(type))
            return new BooleanPojoProperty(type, name);
        if ("long".equals(type))
            return new LongPojoProperty(type, name);
        if ("float".equals(type))
            return new FloatPojoProperty(type, name);
        if ("double".equals(type))
            return new DoublePojoProperty(type, name);
        if (isOtherPrimitive(type))
            return new OtherPrimitivePojoProperty(type, name);
        return new ObjectPojoProperty(type, name);
    }

    private static boolean isOtherPrimitive(String type) {
        return "byte".equals(type) || "char".equals(type) || "short".equals(type)
                || "int".equals(type);
    }

    private final PojoAnnotations annotations = new PojoAnnotations();
    private final String type;
    private final TypeMatcher matcher;
    protected final String name;

    protected PojoProperty(String type, String name) {
        this.type = type;
        this.matcher = new TypeMatcher(type);
        this.name = name;
    }

    public void writeFieldTo(Writer writer) throws IOException {
        annotations.writeTo(writer, 1);
        writer.append("\tprivate final ").append(type).append(" ");
        writer.append(name).append(";\n");
    }

    public void writeGetterTo(Writer writer) throws IOException {
        writer.append("\tpublic ").append(type);
        writer.append(" get").append(Character.toUpperCase(name.charAt(0))).append(
                name.substring(1)).append("() {\n");
        writer.append("\t\treturn ").append(name).append(";\n");
        writer.append("\t}\n");
    }

    public void writeDefaultAssignTo(Writer writer) throws IOException {
        writer.append("\t\tthis.").append(name).append(" = ");
        writer.append(getDefaultValue());
        writer.append(";\n");
    }

    public void writeToStringTo(Writer writer) throws IOException {
        writer.append(name);
    }

    protected abstract String getDefaultValue() throws IOException;

    public abstract void writeHashCodeTo(Writer writer) throws IOException;

    public abstract void writeEqualsTo(Writer writer) throws IOException;

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public List<String> getImportTypesFor(String containerPackage) {
        return matcher.getImportTypesFor(containerPackage);
    }

    public String getRawType() {
        return matcher.getRawType();
    }

    public String getLocalType() {
        return matcher.getLocalType();
    }

    public void annotate(Class<? extends Annotation> type) {
        annotate(type, PojoAnnotations.NO_FIELDS);
    }

    public void annotate(Class<? extends Annotation> type, Map<String, ?> fields) {
        annotations.add(type, fields);
    }
}
