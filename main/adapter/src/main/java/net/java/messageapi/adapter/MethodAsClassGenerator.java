package net.java.messageapi.adapter;

import java.lang.reflect.Method;
import java.util.List;

import javassist.*;

import javax.xml.bind.annotation.*;

import net.java.messageapi.JmsProperty;
import net.java.messageapi.Optional;
import net.java.messageapi.reflection.Parameter;
import net.java.messageapi.reflection.ReflectionAdapter;

import com.google.common.base.Supplier;
import com.google.common.collect.Lists;

public class MethodAsClassGenerator implements Supplier<Class<?>> {

    private final ReflectionAdapter<Method> reflectionAdapter;
    private final List<Parameter> parameters;
    private final ClassPool classPool = ClassPool.getDefault();
    private CtClass ctClass;
    private final Class<?> result;

    public MethodAsClassGenerator(Method method) {
        this.reflectionAdapter = ReflectionAdapter.of(method);
        this.parameters = Parameter.allOf(method);

        this.result = generate();
    }

    protected Class<?> generate() {
        String className = reflectionAdapter.getMethodNameAsFullyQualifiedClassName();

        // try real class
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            // continue
        }

        // try already generated class; can't generate twice
        try {
            this.ctClass = classPool.get(className);
            return ctClass.getClass();
        } catch (NotFoundException e) {
            // continue
        }

        // now really generate
        try {
            generate(className);
            return ctClass.toClass();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void generate(String className) throws Exception {
        this.ctClass = classPool.makeClass(className);
        ctClass.getClassFile().setVersionToJava5();

        List<String> propOrder = addProperties();
        addConstructors();
        addClassAnnotations(propOrder);
    }

    private void addClassAnnotations(List<String> propOrder) {
        CtClassAnnotation xmlRootElement = new CtClassAnnotation(ctClass, XmlRootElement.class);
        xmlRootElement.addMemberValue("name", reflectionAdapter.getMethodName());
        xmlRootElement.set();

        CtClassAnnotation xmlType = new CtClassAnnotation(ctClass, XmlType.class);
        xmlType.addMemberValue("propOrder", propOrder);
        xmlType.set();
    }

    private void addConstructors() throws Exception {
        addDefaultConstuctor();
        addFullConstructor();
    }

    private void addDefaultConstuctor() throws CannotCompileException {
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
            CtField field = addProperty(parameter);

            if (parameter.isAnnotationPresent(JmsProperty.class)) {
                new CtFieldAnnotation(field, JmsProperty.class).set();
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
