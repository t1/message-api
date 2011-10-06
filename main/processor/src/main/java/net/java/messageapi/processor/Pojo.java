package net.java.messageapi.processor;

import java.io.*;
import java.lang.annotation.Annotation;
import java.util.*;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import net.java.messageapi.reflection.DelimiterWriter;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.*;

/**
 * Collects properties to generate the java source for an immutable POJO (plain old java object).
 */
public class Pojo {

    public enum PropertyType {
        REQUIRED {
            @Override
            public Set<String> getImports() {
                return ImmutableSet.of(XmlElement.class.getName());
            }

            @Override
            public void addAnnotationsTo(PojoProperty property) {
                property.annotate(XmlElement.class, ImmutableMap.of("required", true));
            }
        },
        OPTIONAL {
            @Override
            public Set<String> getImports() {
                return ImmutableSet.of(XmlElement.class.getName());
            }

            @Override
            public void addAnnotationsTo(PojoProperty property) {
                property.annotate(XmlElement.class, ImmutableMap.of("required", false));
            }
        },
        TRANSIENT {
            @Override
            public Set<String> getImports() {
                return ImmutableSet.of(XmlTransient.class.getName());
            }

            @Override
            public void addAnnotationsTo(PojoProperty property) {
                property.annotate(XmlTransient.class);
            }
        };

        abstract public Set<String> getImports();

        abstract public void addAnnotationsTo(PojoProperty property);
    }

    private final String pkg;
    private final String className;
    private final Set<String> imports = Sets.newLinkedHashSet();
    private final PojoAnnotations annotations = new PojoAnnotations();
    private final List<PojoProperty> properties = Lists.newArrayList();
    private boolean privateDefaultConstructor;

    public Pojo(String pkg, String className) {
        this.pkg = pkg;
        this.className = className;
    }

    public void writeTo(Writer writer) {
        try {
            writer.append("package ").append(pkg).append(";\n");
            writer.append("\n");
            appendImports(writer);
            writer.append("\n");
            annotations.writeTo(writer, 0);
            writer.append("public class ").append(className).append(" {\n");
            appendFields(writer);
            appendConstructors(writer);
            appendGetters(writer);
            appendEquals(writer);
            appendHashCode(writer);
            appendToString(writer);
            writer.append("}\n");
        } catch (IOException e) {
            throw new RuntimeException("can't write pojo " + className, e);
        }
    }

    private void appendImports(Writer writer) throws IOException {
        for (String type : imports) {
            writer.append("import ").append(type).append(";\n");
        }
    }

    private void appendFields(Writer writer) throws IOException {
        if (properties.isEmpty())
            return;
        writer.append("\n");
        for (PojoProperty property : properties) {
            property.writeFieldTo(writer);
        }
    }

    private void appendConstructors(Writer writer) throws IOException {
        if (properties.isEmpty())
            return;
        if (privateDefaultConstructor) {
            writer.append("\n");
            appendPrivateDefaultConstructor(writer);
        }
        writer.append("\n");
        appendPublicConstructor(writer);
    }

    private void appendPrivateDefaultConstructor(Writer writer) throws IOException {
        writer.append("\t@SuppressWarnings(\"unused\")\n");
        writer.append("\tprivate ").append(className).append("() {\n");
        for (PojoProperty property : properties) {
            property.writeDefaultAssignTo(writer);
        }
        writer.append("\t}\n");
    }

    private void appendPublicConstructor(Writer writer) throws IOException {
        writer.append("\tpublic ").append(className).append("(");
        DelimiterWriter comma = new DelimiterWriter(writer, ", ");
        for (PojoProperty property : properties) {
            String type = property.getLocalType();

            comma.write();
            writer.append(type).append(" ").append(property.getName());
        }
        writer.append(") {\n");
        for (PojoProperty property : properties) {
            String name = property.getName();

            writer.append("\t\tthis.").append(name).append(" = ").append(name);
            writer.append(";\n");
        }
        writer.append("\t}\n");
    }

    private void appendGetters(Writer writer) throws IOException {
        if (properties.isEmpty())
            return;
        writer.append("\n");
        DelimiterWriter newline = new DelimiterWriter(writer, "\n");
        for (PojoProperty property : properties) {
            newline.write();
            property.writeGetterTo(writer);
        }
    }

    private void appendEquals(Writer writer) throws IOException {
        writer.append("\n");
        writer.append("\t@Override\n");
        writer.append("\tpublic boolean equals(Object obj) {\n");
        writer.append("\t\tif (this == obj)\n");
        writer.append("\t\t\treturn true;\n");
        writer.append("\t\tif (obj == null)\n");
        writer.append("\t\t\treturn false;\n");
        writer.append("\t\tif (getClass() != obj.getClass())\n");
        writer.append("\t\t\treturn false;\n");
        if (!properties.isEmpty())
            writer.append("\t\t").append(className).append(" other = (").append(className).append(
                    ") obj;\n");
        for (PojoProperty property : properties) {
            property.writeEqualsTo(writer);
        }
        writer.append("\t\treturn true;\n");
        writer.append("\t}\n");
    }

    private void appendHashCode(Writer writer) throws IOException {
        writer.append("\n");
        writer.append("\t@Override\n");
        writer.append("\tpublic int hashCode() {\n");
        if (properties.isEmpty()) {
            writer.append("\t\treturn 31;\n");
        } else {
            writer.append("\t\tfinal int prime = 31;\n");
            writer.append("\t\tint result = 1;\n");
            for (PojoProperty property : properties) {
                property.writeHashCodeTo(writer);
            }
            writer.append("\t\treturn result;\n");
        }
        writer.append("\t}\n");
    }

    private void appendToString(Writer writer) throws IOException {
        writer.append("\n");
        writer.append("\t@Override\n");
        writer.append("\tpublic String toString() {\n");
        writer.append("\t\treturn \"" + className + "(");
        DelimiterWriter comma = new DelimiterWriter(writer, ", ");
        for (PojoProperty property : properties) {
            comma.write();
            writer.append("\" + ");
            property.writeToStringTo(writer);
            writer.append(" + \"");
        }
        writer.append(")\";\n");
        writer.append("\t}\n");
    }

    public String getSimpleName() {
        return className;
    }

    public String getName() {
        return pkg + "." + className;
    }

    public void annotate(Class<? extends Annotation> type) {
        annotate(type, PojoAnnotations.NO_FIELDS);
    }

    public void annotate(Class<? extends Annotation> type, Map<String, ?> fields) {
        imports.add(type.getName());
        annotations.add(type, fields);
    }

    public void addProperty(String type, String name, boolean required) {
        addProperty(type, name, required ? PropertyType.REQUIRED : PropertyType.OPTIONAL);
    }

    public void addProperty(String type, String name, PropertyType propertyType) {
        PojoProperty property = PojoProperty.create(type, name);

        List<String> importTypes = property.getImportTypesFor(pkg);
        imports.addAll(importTypes);
        properties.add(property);

        imports.addAll(propertyType.getImports());
        propertyType.addAnnotationsTo(property);
    }

    public void addPrivateDefaultConstructor() {
        privateDefaultConstructor = true;
    }

    @Override
    public String toString() {
        StringWriter writer = new StringWriter();
        writeTo(writer);
        return writer.toString();
    }

    public Set<String> getImports() {
        return ImmutableSet.copyOf(imports);
    }

    public String getPackage() {
        return pkg;
    }

    public Map<String, Object> getAnnotation(Class<? extends Annotation> annotationClass) {
        return annotations.getAnnotationFieldsFor(annotationClass);
    }

    public List<PojoProperty> getProperties() {
        return ImmutableList.copyOf(properties);
    }

    @VisibleForTesting
    public PojoProperty getProperty(String name) {
        for (PojoProperty property : properties) {
            if (name.equals(property.getName())) {
                return property;
            }
        }
        throw new IllegalArgumentException("undefined property [" + name + "]");
    }
}
