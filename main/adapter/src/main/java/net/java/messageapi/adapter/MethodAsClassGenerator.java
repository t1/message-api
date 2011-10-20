package net.java.messageapi.adapter;

import java.lang.reflect.Method;
import java.util.List;

import javassist.*;
import javassist.bytecode.*;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.BooleanMemberValue;
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
    private final Class<?> result;

    public MethodAsClassGenerator(Method method) {
        this.reflectionAdapter = ReflectionAdapter.of(method);
        this.parameters = Parameter.allOf(method);

        try {
            // TODO maybe use Nested Class Syntax instead, so the same method name can be defined
            // in several interfaces without collision
            String className = reflectionAdapter.getMethodNameAsFullyQualifiedClassName();
            this.ctClass = classPool.makeClass(className);

            // TODO pojo.annotate(XmlRootElement.class);
            addXmlRootElement();
            addProperties();
            addConstructors();

            this.result = ctClass.toClass();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Class<?> get() {
        return result;
    }

    private void addXmlRootElement() {
        ClassFile classFile = ctClass.getClassFile();
        ConstPool constPool = classFile.getConstPool();
        AnnotationsAttribute attribute = new AnnotationsAttribute(constPool,
                AnnotationsAttribute.visibleTag);
        Annotation annotation = new Annotation("javax.xml.bind.annotation.XmlRootElement",
                constPool);
        attribute.setAnnotation(annotation);
        classFile.addAttribute(attribute);
        ctClass.getClassFile().setVersionToJava5();
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

    private void addProperties() throws Exception {
        // TODO List<String> propOrder = Lists.newArrayList();

        for (Parameter parameter : parameters) {
            Optional optional = parameter.getAnnotation(Optional.class);
            // TODO JmsProperty jmsProperty = parameter.getAnnotation(JmsProperty.class);

            // PojoProperty property =
            boolean required = (optional == null);
            addProperty(parameter, required);

            // if (jmsProperty != null) {
            // property.annotate(JmsProperty.class,
            // ImmutableMap.of("headerOnly", jmsProperty.headerOnly()));
            // }
            //
            // boolean xmlTransient = (jmsProperty != null && jmsProperty.headerOnly());
            // if (xmlTransient) {
            // property.annotate(XmlTransient.class);
            // } else {
            // propOrder.add(name);
            // }
        }

        // String[] propOrderArray = propOrder.toArray(new String[propOrder.size()]);
        // pojo.annotate(XmlType.class, ImmutableMap.of("propOrder", propOrderArray));
    }

    private void addProperty(Parameter parameter, boolean required) throws Exception {
        CtClass type = classPool.get(parameter.getType().getName());
        CtField field = new CtField(type, parameter.getName(), ctClass);
        addXmlElement(field, required);
        ctClass.addField(field);

        CtMethod getter = CtNewMethod.getter("getArg" + parameter.getIndex(), field);
        ctClass.addMethod(getter);
    }

    private void addXmlElement(CtField field, boolean required) {
        FieldInfo fieldInfo = field.getFieldInfo();
        fieldInfo.getConstPool();
        ConstPool constPool = fieldInfo.getConstPool();
        AnnotationsAttribute attribute = new AnnotationsAttribute(constPool,
                AnnotationsAttribute.visibleTag);
        Annotation annotation = new Annotation("javax.xml.bind.annotation.XmlElement", constPool);
        annotation.addMemberValue("required", new BooleanMemberValue(required, constPool));
        attribute.setAnnotation(annotation);
        fieldInfo.addAttribute(attribute);
    }
}
