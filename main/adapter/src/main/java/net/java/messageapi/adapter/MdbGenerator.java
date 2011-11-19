package net.java.messageapi.adapter;

import java.util.List;

import javassist.*;

import javax.ejb.MessageDriven;
import javax.jms.MessageListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Supplier;
import com.google.common.collect.Lists;

public class MdbGenerator implements Supplier<Class<?>> {
    private final Logger logger = LoggerFactory.getLogger(MdbGenerator.class);

    private final ClassPool classPool;
    private final Class<?> result;
    private boolean isGenerated = false;

    public MdbGenerator(Class<?> impl) {
        this.classPool = new ClassPool(true);
        this.classPool.insertClassPath(new LoaderClassPath(impl.getClassLoader()));

        try {
            this.result = generate(impl);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Class<?> generate(Class<?> impl) throws Exception {
        String className = impl.getName() + "MDB";
        // try real class
        try {
            Class<?> realClass = Class.forName(className);
            logger.info("{} already exists... don't generate", className);
            return realClass;
        } catch (ClassNotFoundException e) {
            logger.info("{} is not an existing class", className);
        }

        // try already generated class; can't generate twice
        try {
            Class<? extends CtClass> alreadyGeneratedClass = classPool.get(className).getClass();
            logger.info("{} already generated... don't generate again", className);
            return alreadyGeneratedClass;
        } catch (NotFoundException e) {
            logger.info("{} is not an already generated class", className);
        }

        logger.info("generate a {}", className);
        try {
            isGenerated = true;
            return generate(className, impl);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Class<?> generate(String className, Class<?> impl) throws Exception {
        CtClass ctClass = classPool.makeClass(className);
        ctClass.getClassFile().setVersionToJava5();

        ctClass.setSuperclass(classPool.get(MessageDecoder.class.getName()));

        addDefaultConstructor(ctClass);
        addInjectingConstructor(impl, ctClass);
        addClassAnnotations(ctClass);

        return ctClass.toClass();
    }

    private void addDefaultConstructor(CtClass ctClass) throws Exception {
        CtConstructor defaultConstructor = new CtConstructor(new CtClass[0], ctClass);
        defaultConstructor.setBody("{ super(null, null);\n"
                + "throw new UnsupportedOperationException(\"this constructor exists only to satisfy the required bean lifecycle... it's never called\");\n"
                + "}");
        ctClass.addConstructor(defaultConstructor);
    }

    private void addInjectingConstructor(Class<?> impl, CtClass ctClass) throws Exception {
        // @Inject
        // public ReceiverMdb(@JmsIncoming CustomerServiceImpl impl) {
        // super(CustomerService.class, impl);
        // }
        List<CtClass> argTypes = Lists.newArrayList();
        StringBuilder constructorBody = new StringBuilder("{ super(null,null");
        // CustomerService.class, impl);
        // for (Parameter parameter : parameters) {
        argTypes.add(classPool.get(impl.getName()));
        // constructorBody.append("this.").append(parameter.getName());
        // constructorBody.append(" = $").append(parameter.getIndex() + 1).append(";");
        // }
        constructorBody.append("); }");

        CtClass[] argTypeArray = argTypes.toArray(new CtClass[argTypes.size()]);
        CtConstructor constructor = new CtConstructor(argTypeArray, ctClass);
        constructor.setBody(constructorBody.toString());
        ctClass.addConstructor(constructor);
    }

    private void addClassAnnotations(CtClass ctClass) {
        // @MessageDriven(messageListenerInterface = MessageListener.class, //
        // activationConfig = { @ActivationConfigProperty(propertyName = "destination",
        // propertyValue =
        // "jmskata.messaging.CustomerService") })
        CtClassAnnotation messageDriven = new CtClassAnnotation(ctClass, MessageDriven.class);
        messageDriven.addMemberValue("messageListenerInterface", MessageListener.class);
        // messageDriven.addMemberValue("activationConfig", createActivationConfig(ctClass));
        messageDriven.set();
    }

    // private MemberValue createActivationConfig(CtClass ctClass) {
    // ConstPool constPool = ctClass.getClassFile().getConstPool();
    // ArrayMemberValue activationConfig = new ArrayMemberValue(constPool);
    //
    // Annotation annotation = null;
    // AnnotationMemberValue destination = new AnnotationMemberValue(annotation, constPool);
    //
    // MemberValue[] values = { destination };
    //
    // activationConfig.setValue(values);
    // return activationConfig;
    // }

    @Override
    public Class<?> get() {
        return result;
    }

    public boolean isGenerated() {
        return isGenerated;
    }
}
