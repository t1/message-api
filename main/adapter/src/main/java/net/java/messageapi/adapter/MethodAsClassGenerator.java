package net.java.messageapi.adapter;

import java.lang.reflect.Method;
import java.util.List;

import javassist.*;
import net.java.messageapi.reflection.Parameter;
import net.java.messageapi.reflection.ReflectionAdapter;

import com.google.common.collect.Lists;

class MethodAsClassGenerator {

    private final ReflectionAdapter<Method> reflectionAdapter;
    private final List<Parameter> parameters;
    private final ClassPool classPool = ClassPool.getDefault();

    public MethodAsClassGenerator(Method method) {
        this.reflectionAdapter = ReflectionAdapter.of(method);
        this.parameters = Parameter.allOf(method);
    }

    public <T> Class<T> generate() {
        try {
            // TODO maybe use Nested Class Syntax instead, so the same method name can be defined
            // in several interfaces without collision
            String className = reflectionAdapter.getMethodNameAsFullyQualifiedClassName();
            CtClass ctClass = classPool.makeClass(className);

            // TODO pojo.annotate(XmlRootElement.class);
            addProperties(ctClass);
            addConstructors(ctClass);

            @SuppressWarnings("unchecked")
            Class<T> result = ctClass.toClass();
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void addConstructors(CtClass pojo) throws Exception {
        List<CtClass> argTypes = Lists.newArrayList();
        StringBuilder constructorBody = new StringBuilder("{ super();");
        for (Parameter parameter : parameters) {
            argTypes.add(classPool.get(parameter.getType().getName()));
            constructorBody.append("this.").append(parameter.getName());
            constructorBody.append(" = $").append(parameter.getIndex() + 1).append(";");
        }

        CtClass[] argTypeArray = argTypes.toArray(new CtClass[argTypes.size()]);
        CtConstructor constructor = new CtConstructor(argTypeArray, pojo);
        constructorBody.append("}");
        constructor.setBody(constructorBody.toString());
        pojo.addConstructor(constructor);

        // TODO pojo.addPrivateDefaultConstructor();
    }

    private void addProperties(CtClass pojo) throws Exception {
        // TODO List<String> propOrder = Lists.newArrayList();

        for (Parameter parameter : parameters) {
            // TODO Optional optional = parameter.getAnnotation(Optional.class);
            // TODO JmsProperty jmsProperty = parameter.getAnnotation(JmsProperty.class);

            // PojoProperty property =
            addProperty(pojo, parameter);

            // if (jmsProperty != null) {
            // property.annotate(JmsProperty.class,
            // ImmutableMap.of("headerOnly", jmsProperty.headerOnly()));
            // }
            //
            // boolean xmlTransient = (jmsProperty != null && jmsProperty.headerOnly());
            // if (xmlTransient) {
            // property.annotate(XmlTransient.class);
            // } else {
            // boolean required = (optional == null);
            // property.annotate(XmlElement.class, ImmutableMap.of("required", required));
            // propOrder.add(name);
            // }
        }

        // String[] propOrderArray = propOrder.toArray(new String[propOrder.size()]);
        // pojo.annotate(XmlType.class, ImmutableMap.of("propOrder", propOrderArray));
    }

    private void addProperty(CtClass pojo, Parameter parameter) throws Exception {
        CtClass type = classPool.get(parameter.getType().getName());
        CtField field = new CtField(type, parameter.getName(), pojo);
        pojo.addField(field);
        CtMethod getter = CtNewMethod.getter("getArg" + parameter.getIndex(), field);
        pojo.addMethod(getter);
    }
}
