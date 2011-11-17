package net.java.messageapi.processor;

import java.io.*;
import java.util.List;

import javax.annotation.Generated;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeKind;
import javax.tools.JavaFileObject;
import javax.xml.bind.annotation.*;

import net.java.messageapi.JmsProperty;
import net.java.messageapi.Optional;
import net.java.messageapi.pojo.Pojo;
import net.java.messageapi.pojo.PojoProperty;
import net.java.messageapi.reflection.ReflectionAdapter;

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

        public boolean isUnique() {
            return reflection.isUnique();
        }

        public ExecutableElement getElement() {
            return method;
        }

        public String getPackage() {
            return reflection.getPackage();
        }

        public String getMethodName() {
            return reflection.getMethodName();
        }

        public String getMethodNameAsClassName() {
            return reflection.getMethodNameAsClassName();
        }

        public List<VariableElement> getParameters() {
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

    @Override
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

        String pkg = method.getPackage();
        String container = method.getContainingClassName();
        assert container.startsWith(pkg + ".");
        container = container.substring(pkg.length() + 1);
        container = container.replace('.', '$');
        String className = container + "$" + method.getMethodNameAsClassName();
        Pojo pojo = new Pojo(pkg, className);
        pojo.addInterface(Serializable.class);
        addAnnotations(method, pojo);
        addProperties(method, pojo);
        pojo.addPrivateDefaultConstructor();

        return pojo;
    }

    private void addAnnotations(MethodAdapter method, Pojo pojo) {
        pojo.annotate(Generated.class,
                ImmutableMap.of("value", MessageApiAnnotationProcessor.class.getName()));
        pojo.annotate(XmlRootElement.class, ImmutableMap.of("name", method.getMethodName()));
    }

    private void addProperties(MethodAdapter method, Pojo pojo) {
        List<String> propOrder = Lists.newArrayList();

        for (VariableElement parameter : method.getParameters()) {
            String type = parameter.asType().toString();
            String name = getParameterName(parameter);

            Optional optional = parameter.getAnnotation(Optional.class);
            PojoProperty property = pojo.addProperty(type, name);

            if (parameter.getAnnotation(JmsProperty.class) != null) {
                property.annotate(JmsProperty.class);
                property.setTransient();
            } else {
                boolean required = (optional == null);
                property.annotate(XmlElement.class, ImmutableMap.of("required", required));
                propOrder.add(name);
            }
        }

        String[] propOrderArray = propOrder.toArray(new String[propOrder.size()]);
        pojo.annotate(XmlType.class, ImmutableMap.of("propOrder", propOrderArray));
    }

    private String getParameterName(VariableElement parameter) {
        return parameter.getSimpleName().toString();
    }

    @VisibleForTesting
    List<Pojo> getGeneratedPojos() {
        return generatedPojos;
    }
}
