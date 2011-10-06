package net.java.messageapi.processor;

import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Map.Entry;

import net.java.messageapi.reflection.DelimiterWriter;

import com.google.common.collect.*;

class PojoAnnotations {

    public static final Map<String, Object> NO_FIELDS = ImmutableMap.of();

    private static final ImmutableSet<Class<?>> BOXED_PRIMITIVES = ImmutableSet.<Class<?>> of(
            Boolean.class, Byte.class, Short.class, Character.class, Integer.class, Long.class,
            Float.class, Double.class);

    private final Map<Class<Annotation>, Map<String, Object>> annotations = Maps.newLinkedHashMap();

    @SuppressWarnings("unchecked")
    public void add(Class<? extends Annotation> type, Map<String, ?> fields) {
        annotations.put((Class<Annotation>) type, (Map<String, Object>) fields);
    }

    public Map<String, Object> getAnnotationFieldsFor(Class<? extends Annotation> annotationClass) {
        return annotations.get(annotationClass);
    }

    public void writeTo(Writer writer, int indent) throws IOException {
        for (Entry<Class<Annotation>, Map<String, Object>> annotation : annotations.entrySet()) {
            writer.append(indentOf(indent));
            writer.append("@").append(annotation.getKey().getSimpleName());
            appendAnnotationFields(writer, annotation.getValue(), indent + 1);
            writer.append("\n");
        }
    }

    private String indentOf(int indent) {
        return "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t".substring(0, indent);
    }

    private void appendAnnotationFields(Writer writer, Map<String, Object> fields, int indent)
            throws IOException {
        if (fields.isEmpty()) {
            // write nothing
        } else {
            writer.append('(');
            boolean writeFieldNames = !isOnlyValueField(fields);
            DelimiterWriter separation = new DelimiterWriter(writer, ", ");
            for (Entry<String, Object> entry : fields.entrySet()) {
                separation.write();
                if (writeFieldNames) {
                    if (fields.size() > 1)
                        writer.append("\n").append(indentOf(indent));
                    writer.append(entry.getKey()).append(" = ");
                }
                appendAnnotationFieldValue(writer, entry.getValue());
            }
            writer.append(')');
        }
    }

    private boolean isOnlyValueField(Map<String, Object> fields) {
        return fields.size() == 1 && "value".equals(fields.keySet().iterator().next());
    }

    private void appendAnnotationFieldValue(Writer writer, Object valueArg) throws IOException {
        boolean isArray = valueArg.getClass().isArray();
        if (isArray) {
            writer.append('{');
        }

        DelimiterWriter comma = new DelimiterWriter(writer, ", ");
        Object[] allValues = isArray ? (Object[]) valueArg : new Object[] { valueArg };
        for (Object value : allValues) {
            comma.write();
            boolean mustQuote = !isPrimitive(value.getClass());

            if (mustQuote) {
                writer.append('"');
            }

            writer.append(value.toString());

            if (mustQuote) {
                writer.append('"');
            }
        }

        if (isArray) {
            writer.append('}');
        }
    }

    private boolean isPrimitive(Class<?> type) {
        return type.isPrimitive() || BOXED_PRIMITIVES.contains(type);
    }
}
