package net.java.messageapi.processor;

import java.io.IOException;
import java.io.Writer;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.*;
import javax.tools.FileObject;

import net.java.messageapi.reflection.DelimiterWriter;
import net.java.messageapi.reflection.ParameterMapParser;

import org.joda.time.Instant;

import com.google.common.collect.ImmutableList;

/**
 * Generates the <code>.parametermap</code> files for the
 * {@link net.java.messageapi.reflection.Parameter Parameter} class.
 */
public class ParameterMapGenerator extends AbstractGenerator {

    public ParameterMapGenerator(Messager messager, Filer filer) {
        super(messager, filer);
    }

    public void process(Element element) {
        TypeElement type = (TypeElement) element;
        String fileName = type.getSimpleName() + ParameterMapParser.SUFFIX;
        String pkg = getPackageOf(type);

        try {
            FileObject file = createResourceFile(pkg, fileName, ImmutableList.of(type));
            Writer writer = file.openWriter();
            try {
                writeParameterMap(type, writer);
            } finally {
                writer.close();
            }
        } catch (IOException e) {
            error("can't write parameter map for " + type + ": " + e);
        }
    }

    private void writeParameterMap(TypeElement type, Writer writer) throws IOException {
        note("Writing " + ParameterMapParser.SUFFIX + " for " + type.getQualifiedName());

        writer.append("# generated from ").append(type.getQualifiedName());
        writer.append(" at ").append(new Instant().toString()).append('\n');
        for (Element element : type.getEnclosedElements()) {
            if (element.getKind() == ElementKind.METHOD) {
                writeMethod((ExecutableElement) element, writer);
            }
        }
    }

    private void writeMethod(ExecutableElement element, Writer writer) throws IOException {
        writer.append(element.getSimpleName()).append('(');
        DelimiterWriter comma = new DelimiterWriter(writer, ", ");
        for (VariableElement parameter : element.getParameters()) {
            comma.write();

            String typeName = parameter.asType().toString();
            Name parameterName = parameter.getSimpleName();
            writer.append(typeName).append(' ').append(parameterName);
        }
        writer.append(")\n");
    }
}
