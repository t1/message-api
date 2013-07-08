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
 * <p>
 * TODO make it work with primitive types
 * 
 * @param <T>
 *            the type to proxy
 */
public abstract class InvocationProxy<T> {

    /** The directory to store all generated proxy classes to for debug purposes, or null */
    private static final String generatedProxies = "target/generated-proxies";

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
        if (generatedProxies != null)
            proxyType.debugWriteFile(generatedProxies);
        return proxyType.toClass();
    }

    private void generate(String className) throws CannotCompileException, NotFoundException {
        proxyType = classPool.makeClass(className);
        proxyType.getClassFile().setVersionToJava5();

        proxyType.setSuperclass(targetType);

        addProxyField();
        addConstuctor();
        forwardMethods();
    }

    private void addProxyField() throws CannotCompileException, NotFoundException {
        CtClass type = classPool.get(InvocationProxy.class.getName());
        CtField field = new CtField(type, "proxy", proxyType);
        field.setModifiers(Modifier.PRIVATE | Modifier.FINAL);
        field.setGenericSignature(encode(type) + "<" + encode(targetType) + ";>;");
        proxyType.addField(field);
    }

    private String encode(CtClass type) {
        return "L" + type.getName().replace(".", "/");
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
            CtClass[] parameterTypes = targetMethod.getParameterTypes();
            if (parameterTypes.length != 1)
                continue;
            log.debug("override {}", targetMethod.getLongName());
            CtMethod method =
                    new CtMethod(targetMethod.getReturnType(), targetMethod.getName(), parameterTypes, proxyType);
            method.setBody("{" //
                    + "java.lang.reflect.Method method = "
                    + targetType.getName()
                    + ".class.getMethod(\"customerCreated\", new Class[]{" + argTypes(parameterTypes) + "});\n" //
                    // + "System.out.println(\":::: \" + method);" //
                    + "proxy.invoke(method, new Object[]{" + args(parameterTypes.length) + "});\n" //
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

    private String argTypes(CtClass[] parameterTypes) {
        StringBuilder types = new StringBuilder();
        for (CtClass parameterType : parameterTypes) {
            types.append(parameterType.getName() + ".class");
        }
        return types.toString();
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

    public abstract Object invoke(Method method, Object... args);
}
