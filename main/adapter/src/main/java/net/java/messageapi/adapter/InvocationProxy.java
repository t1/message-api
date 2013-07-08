package net.java.messageapi.adapter;

import java.lang.reflect.Method;

import javassist.*;

import org.slf4j.*;

/**
 * Similar to a {@link java.lang.reflect.Proxy}, only that it works with classes as well as interfaces; the only
 * restriction is that the class and the methods to be forwarded must not be final and the class must have a non-arg
 * constructor. Instead of an extra {@link java.lang.reflect.InvocationHandler invocation handler}, all calls are
 * handled by an abstract method for you to override.
 * <p>
 * TODO find out if this can be done with a CDI interceptor
 * 
 * @param <T>
 *            the type to proxy
 */
public abstract class InvocationProxy<T> {

    private final Logger log = LoggerFactory.getLogger(InvocationProxy.class);

    private final ClassPool classPool;
    private final CtClass targetType;
    private CtClass proxyType;

    public InvocationProxy(Class<T> targetType) {
        this.classPool = getClassPool(targetType);
        this.targetType = getTargetType(targetType);
    }

    private ClassPool getClassPool(Class<T> targetType) {
        ClassPool pool = new ClassPool(true);
        ClassLoader classLoader = targetType.getClassLoader();
        pool.insertClassPath(new LoaderClassPath(classLoader));
        return pool;
    }

    private CtClass getTargetType(Class<T> targetType) {
        try {
            return classPool.get(targetType.getName());
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public T cast() {
        String className = targetType.getName() + "$$InvocationProxy";
        try {
            return proxyType(className).getConstructor(InvocationProxy.class).newInstance(this);
        } catch (Exception e) {
            throw new RuntimeException("can't generate " + className, e);
        }
    }

    @SuppressWarnings("unchecked")
    private Class<T> proxyType(String className) throws CannotCompileException, NotFoundException {
        if (proxyType != null) {
            return proxyType.toClass();
        }

        // try real class
        try {
            return (Class<T>) Class.forName(className);
        } catch (ClassNotFoundException e) {
            // continue
        }

        // try already generated class; can't generate twice
        try {
            this.proxyType = classPool.get(className);
            return (Class<T>) proxyType.getClass();
        } catch (NotFoundException e) {
            // continue
        }

        // now really generate
        generate(className);
        log.debug("write");
        proxyType.debugWriteFile("xxx");
        log.debug("written");
        return proxyType.toClass();
    }

    // public class CallbackTest$$InvocationProxy extends CallbackTest {
    // private final InvocationProxy<CallbackTest> proxy;
    //
    // public CallbackTest$$InvocationProxy(InvocationProxy<CallbackTest> proxy) {
    // this.proxy = proxy;
    // }
    //
    // @Override
    // public void customerCreated(long createdCustomerId) {
    // try {
    // Method method = CallbackTest.class.getMethod("customerCreated", Long.TYPE);
    // proxy.invoke(method, createdCustomerId);
    // } catch (NoSuchMethodException e) {
    // throw new RuntimeException(e);
    // }
    // }
    // }

    private void generate(String className) throws CannotCompileException, NotFoundException {
        proxyType = classPool.makeClass(className);
        proxyType.getClassFile().setVersionToJava5();

        proxyType.setSuperclass(targetType);

        addProxyField();
        addConstuctor();
        forwardMethods();
    }

    private void addProxyField() throws CannotCompileException, NotFoundException {
        CtClass type = classPool.get("net.java.messageapi.adapter.InvocationProxy");
        CtField field = new CtField(type, "proxy", proxyType);
        field.setModifiers(Modifier.PRIVATE | Modifier.FINAL);
        field.setGenericSignature("Lnet/java/messageapi/adapter/InvocationProxy<Lnet/java/messageapi/adapter/CallbackTest;>;");
        proxyType.addField(field);
    }

    private void addConstuctor() throws CannotCompileException, NotFoundException {
        CtConstructor constructor =
                new CtConstructor(new CtClass[] { classPool.get(InvocationProxy.class.getName()) }, proxyType);
        constructor.setBody("{ this.proxy = $1; }");
        proxyType.addConstructor(constructor);
    }

    private void forwardMethods() throws NotFoundException, CannotCompileException {
        for (CtMethod targetMethod : targetType.getMethods()) {
            if (isStatic(targetMethod) || isFinal(targetMethod) || isObjectMethod(targetMethod))
                continue;
            log.debug("override {}", targetMethod.getLongName());
            CtClass[] parameterTypes = targetMethod.getParameterTypes();
            if (parameterTypes.length == 0)
                continue;
            CtMethod method =
                    new CtMethod(targetMethod.getReturnType(), targetMethod.getName(), parameterTypes, proxyType);
            StringBuilder args = args(parameterTypes.length);
            method.setBody("{" //
                    + "try {\n" //
                    + "java.lang.reflect.Method method = "
                    + "net.java.messageapi.adapter.CallbackTest.class.getMethod(\"customerCreated\", new Class[]{Long.TYPE});\n" //
                    + "System.out.println(\":::: \" + method);" //
                    + "proxy.invoke(method, new Object[] {" + args + "});\n" //
                    + "} catch (NoSuchMethodException e) {\n" //
                    + "throw new RuntimeException(e);\n" //
                    + "}\n" //
                    + "}");
            proxyType.addMethod(method);
        }
    }

    private boolean isStatic(CtMethod targetMethod) {
        return Modifier.isStatic(targetMethod.getModifiers());
    }

    private boolean isFinal(CtMethod targetMethod) {
        return Modifier.isFinal(targetMethod.getModifiers());
    }

    private boolean isObjectMethod(CtMethod targetMethod) {
        return "java.lang.Object".equals(targetMethod.getDeclaringClass().getName());
    }

    private StringBuilder args(int count) {
        StringBuilder args = new StringBuilder();
        for (int i = 1; i <= count; i++) {
            if (i > 1)
                args.append(", ");
            args.append("$").append(i++);
        }
        return args;
    }

    public abstract Object invoke(Method method, Object[] args);
}
