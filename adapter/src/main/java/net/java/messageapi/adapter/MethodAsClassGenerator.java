package net.java.messageapi.adapter;

import java.lang.reflect.Method;
import java.util.*;

import javassist.*;

import javax.xml.bind.annotation.*;

import net.java.messageapi.*;
import net.java.messageapi.adapter.javassist.*;
import net.java.messageapi.reflection.*;

import org.slf4j.*;

public class MethodAsClassGenerator {
    private static final Map<Method, Class<?>> CACHE = new HashMap<Method, Class<?>>();

    public static Class<?> of(Method method) {
        Class<?> type = CACHE.get(method);
        if (type == null) {
            type = new MethodAsClassGenerator(method).get();
            CACHE.put(method, type);
        }
        return type;
    }

    private final Logger log = LoggerFactory.getLogger(MethodAsClassGenerator.class);

    private final ReflectionAdapter<Method> reflectionAdapter;
    private final List<Parameter> parameters;
    private final ClassPool classPool;
    private CtClass ctClass;
    private final Class<?> result;
    private final List<String> xmlTypePropOrder = new ArrayList<String>();
    private final List<String> totalPropOrder = new ArrayList<String>();

    MethodAsClassGenerator(Method method) {
        this.classPool = getClassPool(method);
        this.reflectionAdapter = ReflectionAdapter.of(method);
        this.parameters = Parameter.allOf(method);

        this.result = generate();
    }

    private ClassPool getClassPool(Method method) {
        ClassPool pool = new ClassPool(true);
        ClassLoader classLoader = method.getDeclaringClass().getClassLoader();
        log.info("generate class for {} in {}", method, classLoader);
        pool.insertClassPath(new LoaderClassPath(classLoader));
        return pool;
    }

    private Class<?> generate() {
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

    private void generate(String className) throws Exception {
        this.ctClass = classPool.makeClass(className);
        ctClass.getClassFile().setVersionToJava5();

        addProperties();
        addConstructors();
        addClassAnnotations();
    }

    private void addClassAnnotations() {
        CtClassAnnotation xmlRootElementAnnotation = new CtClassAnnotation(ctClass, XmlRootElement.class);
        xmlRootElementAnnotation.addMemberValue("name", reflectionAdapter.getMethodName());
        xmlRootElementAnnotation.set();

        CtClassAnnotation totalPropOrderAnnotation = new CtClassAnnotation(ctClass, PropOrder.class);
        totalPropOrderAnnotation.addMemberValue("value", totalPropOrder);
        totalPropOrderAnnotation.set();

        CtClassAnnotation xmlTypeAnnotation = new CtClassAnnotation(ctClass, XmlType.class);
        xmlTypeAnnotation.addMemberValue("propOrder", xmlTypePropOrder);
        xmlTypeAnnotation.set();
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
        List<CtClass> argTypes = new ArrayList<CtClass>();
        String constructorBody = constructorBody(argTypes);

        CtClass[] argTypeArray = argTypes.toArray(new CtClass[argTypes.size()]);
        CtConstructor constructor = new CtConstructor(argTypeArray, ctClass);
        constructor.setBody(constructorBody.toString());
        ctClass.addConstructor(constructor);
    }

    private String constructorBody(List<CtClass> argTypes) throws NotFoundException {
        StringBuilder constructorBody = new StringBuilder("{ super();");
        for (Parameter parameter : parameters) {
            argTypes.add(classPool.get(parameter.getType().getName()));
            constructorBody.append("this.").append(parameter.getName());
            constructorBody.append(" = $").append(parameter.getIndex() + 1).append(";");
        }
        constructorBody.append("}");
        return constructorBody.toString();
    }

    // TODO generate toString, hashCode and equals

    private void addProperties() throws Exception {
        for (Parameter parameter : parameters) {
            Optional optional = parameter.getAnnotation(Optional.class);
            CtField field = addProperty(parameter);

            if (parameter.isAnnotationPresent(JmsProperty.class)) {
                new CtFieldAnnotation(field, JmsProperty.class).set();
                field.setModifiers(field.getModifiers() | Modifier.TRANSIENT);
            } else {
                CtFieldAnnotation xmlElement = new CtFieldAnnotation(field, XmlElement.class);
                xmlElement.addMemberValue("required", (optional == null));
                xmlElement.set();
                xmlTypePropOrder.add(field.getName());
            }
            totalPropOrder.add(field.getName());
        }
    }

    private CtField addProperty(Parameter parameter) throws Exception {
        CtClass type = classPool.get(parameter.getType().getName());
        CtField field = new CtField(type, parameter.getName(), ctClass);
        ctClass.addField(field);

        CtMethod getter = CtNewMethod.getter("getArg" + parameter.getIndex(), field);
        ctClass.addMethod(getter);

        return field;
    }

    public Class<?> get() {
        return result;
    }
}
