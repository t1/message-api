package net.java.messageapi.processor;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import javax.annotation.Generated;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeKind;
import javax.tools.JavaFileObject;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import net.java.messageapi.Optional;
import net.java.messageapi.processor.pojo.Pojo;
import net.java.messageapi.reflection.ReflectionAdapter;

import org.joda.time.Instant;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

public class PojoGenerator extends AbstractGenerator {

    private static class MethodAdapter {
        private final ExecutableElement method;
        private final ReflectionAdapter<?> reflection;

        public MethodAdapter(ExecutableElement method) {
            this.method = method;
            this.reflection = ReflectionAdapter.of(method);
        }

        public String[] getPropOrder() {
            List<String> result = Lists.newArrayList();
            for (VariableElement variable : method.getParameters()) {
                result.add(variable.getSimpleName().toString());
            }
            return result.toArray(new String[result.size()]);
        }

        public boolean isUnique() {
            return reflection.isUnique();
        }

        public ExecutableElement getElement() {
            return method;
        }

        public String getPackage() {
            return reflection.getPackage();
        }

        public String getMethodNameAsClassName() {
            return reflection.getMethodNameAsClassName();
        }

        public List<VariableElement> getParameters() {
            @SuppressWarnings("unchecked")
            List<VariableElement> parameters = (List<VariableElement>) method.getParameters();
            return parameters;
        }

        public String getContainingClassName() {
            Element enclosingElement = method.getEnclosingElement();
            if (enclosingElement instanceof TypeElement)
                return ((TypeElement) enclosingElement).getQualifiedName().toString();
            return enclosingElement.getSimpleName().toString();
        }
    }

    private final List<Pojo> generatedPojos = Lists.newArrayList();

    public PojoGenerator(Messager messager, Filer filer) {
        super(messager, filer);
    }

    public void process(Element messageApi) {
        TypeElement type = checkType(messageApi);
        if (type != null) {
            processType(type);
        }
    }

    private TypeElement checkType(Element element) {
        if (ElementKind.INTERFACE != element.getKind()) {
            error("The MessageApi annotation can only be put on an interface, not on a "
                    + element.getKind(), element);
            return null;
        }
        if (element.getEnclosedElements().isEmpty()) {
            error("MessageApi must have methods", element);
            return null;
        }
        return (TypeElement) element; // @MessageApi is @Target(TYPE)
    }

    private void processType(TypeElement type) {
        note("Processing MessageApi [" + type.getQualifiedName() + "]");
        for (Element enclosedElement : type.getEnclosedElements()) {
            if (enclosedElement instanceof ExecutableElement) {
                ExecutableElement method = (ExecutableElement) enclosedElement;
                if (checkMethod(method)) {
                    try {
                        generatedPojos.add(createPojoFor(method));
                    } catch (RuntimeException e) {
                        error("can't generate pojo: " + e, method);
                    }
                }
            }
        }
    }

    private boolean checkMethod(ExecutableElement executable) {
        if (TypeKind.VOID != executable.getReturnType().getKind()) {
            error("MessageApi methods must return void; they are asynchronous!", executable);
            return false;
        }
        if (!executable.getThrownTypes().isEmpty()) {
            error("MessageApi methods must not declare an exception; they are asynchronous!",
                    executable);
            return false;
        }
        return true;
    }

    private Pojo createPojoFor(ExecutableElement method) {
        Pojo pojo = createPojo(method);

        note("Writing " + pojo.getName());

        Writer writer = null;
        try {
            JavaFileObject sourceFile = createSourceFile(pojo.getName(), method);
            writer = sourceFile.openWriter();
            pojo.writeTo(writer);
        } catch (IOException e) {
            error("Can't write MessageApi pojo\n" + e, method);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }

        return pojo;
    }

    private Pojo createPojo(ExecutableElement executableElement) {
        MethodAdapter method = new MethodAdapter(executableElement);
        if (!method.isUnique())
            warn("ambiguous method name; it's generally better to use unique names "
                    + "and not rely on the parameter type mangling.", method.getElement());

        Pojo pojo = new Pojo(method.getPackage(), method.getMethodNameAsClassName());
        addAnnotations(method, pojo);
        addProperties(method, pojo);
        pojo.addPrivateDefaultConstructor();

        return pojo;
    }

    private void addAnnotations(MethodAdapter method, Pojo pojo) {
        pojo.annotate(Generated.class, ImmutableMap.of("value",
                MessageApiAnnotationProcessor.class.getName(), "date", new Instant(), "comments",
                "from " + method.getContainingClassName()));
        pojo.annotate(XmlRootElement.class);
        pojo.annotate(XmlType.class, ImmutableMap.of("propOrder", method.getPropOrder()));
    }

    private void addProperties(MethodAdapter method, Pojo pojo) {
        for (VariableElement parameter : method.getParameters()) {
            String type = parameter.asType().toString();
            String name = getParameterName(parameter);
            boolean required = !isOptional(parameter);
            pojo.addProperty(type, name, required);
        }
    }

    private String getParameterName(VariableElement parameter) {
        return parameter.getSimpleName().toString();
    }

    private boolean isOptional(VariableElement parameter) {
        return parameter.getAnnotation(Optional.class) != null;
    }

    @VisibleForTesting
    List<Pojo> getGeneratedPojos() {
        return generatedPojos;
    }
}
