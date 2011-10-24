package net.java.messageapi.adapter;

import java.lang.reflect.Method;
import java.util.List;

import javassist.*;
import javassist.bytecode.ClassFile;

import javax.xml.bind.annotation.*;

import net.java.messageapi.JmsProperty;
import net.java.messageapi.Optional;
import net.java.messageapi.reflection.Parameter;
import net.java.messageapi.reflection.ReflectionAdapter;

import com.google.common.base.Supplier;
import com.google.common.collect.Lists;

class MethodAsClassGenerator implements Supplier<Class<?>> {

    private final ReflectionAdapter<Method> reflectionAdapter;
    private final List<Parameter> parameters;
    private final ClassPool classPool = ClassPool.getDefault();
    private final CtClass ctClass;
    private ClassFile classFile;
    private final Class<?> result;

    public MethodAsClassGenerator(Method method) {
        this.reflectionAdapter = ReflectionAdapter.of(method);
        this.parameters = Parameter.allOf(method);

        try {
            // TODO maybe use Nested Class Syntax instead, so the same method name can be defined
            // in several interfaces without collision
            String className = reflectionAdapter.getMethodNameAsFullyQualifiedClassName();
            this.ctClass = classPool.makeClass(className);
            this.classFile = ctClass.getClassFile();

            classFile.setVersionToJava5();

            List<String> propOrder = addProperties();
            addConstructors();
            addClassAnnotations(propOrder);

            this.result = ctClass.toClass();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void addClassAnnotations(List<String> propOrder) {
        new CtClassAnnotation(ctClass, XmlRootElement.class).set();

        CtClassAnnotation xmlType = new CtClassAnnotation(ctClass, XmlType.class);
        xmlType.addMemberValue("propOrder", propOrder);
        xmlType.set();
    }

    private void addConstructors() throws Exception {
        addDefaultConstuctor();
        addFullConstructor();
    }

    private void addDefaultConstuctor() throws NotFoundException, CannotCompileException {
        CtConstructor constructor = new CtConstructor(new CtClass[0], ctClass);
        constructor.setBody("{ super(); }");
        constructor.setModifiers(Modifier.PRIVATE);
        ctClass.addConstructor(constructor);
    }

    private void addFullConstructor() throws NotFoundException, CannotCompileException {
        List<CtClass> argTypes = Lists.newArrayList();
        StringBuilder constructorBody = new StringBuilder("{ super();");
        for (Parameter parameter : parameters) {
            argTypes.add(classPool.get(parameter.getType().getName()));
            constructorBody.append("this.").append(parameter.getName());
            constructorBody.append(" = $").append(parameter.getIndex() + 1).append(";");
        }

        CtClass[] argTypeArray = argTypes.toArray(new CtClass[argTypes.size()]);
        CtConstructor constructor = new CtConstructor(argTypeArray, ctClass);
        constructorBody.append("}");
        constructor.setBody(constructorBody.toString());
        ctClass.addConstructor(constructor);
    }

    private List<String> addProperties() throws Exception {
        List<String> propOrder = Lists.newArrayList();

        for (Parameter parameter : parameters) {
            Optional optional = parameter.getAnnotation(Optional.class);
            JmsProperty jmsProperty = parameter.getAnnotation(JmsProperty.class);

            CtField field = addProperty(parameter);

            if (jmsProperty != null) {
                new CtFieldAnnotation(field, jmsProperty).set();
            }

            boolean xmlTransient = (jmsProperty != null && jmsProperty.headerOnly());
            if (xmlTransient) {
                new CtFieldAnnotation(field, XmlTransient.class).set();
            } else {
                CtFieldAnnotation xmlElement = new CtFieldAnnotation(field, XmlElement.class);
                xmlElement.addMemberValue("required", (optional == null));
                xmlElement.set();
                propOrder.add(field.getName());
            }
        }

        return propOrder;
    }

    private CtField addProperty(Parameter parameter) throws Exception {
        CtClass type = classPool.get(parameter.getType().getName());
        CtField field = new CtField(type, parameter.getName(), ctClass);
        ctClass.addField(field);

        CtMethod getter = CtNewMethod.getter("getArg" + parameter.getIndex(), field);
        ctClass.addMethod(getter);

        return field;
    }

    @Override
    public Class<?> get() {
        return result;
    }
}
