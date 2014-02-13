package net.java.messageapi.adapter;

import java.lang.reflect.*;

import javassist.*;
import javassist.Modifier;

import org.slf4j.*;

/**
 * Similar to a {@link java.lang.reflect.Proxy}, only that it works with classes as well as interfaces; the only
 * restriction is that the class must be static and the class and the methods to be forwarded must not be final nor
 * private, and the class must have a constructor that is not private or have arguments. Instead of an extra
 * {@link java.lang.reflect.InvocationHandler invocation handler} , all calls are handled by an
 * {@link #invoke(Method, Object...) abstract method} for you to override.
 * <p>
 * <b>Note:</b> Primitive parameter and return types are <b>not implemented, yet.</b>
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
        if (Modifier.isFinal(targetType.getModifiers()))
            throw new IllegalArgumentException("classes to be proxied must not be final: " + targetType.getName());
        if (targetType.isLocalClass())
            throw new IllegalArgumentException("classes to be proxied must not be local: " + targetType.getName());
        if (Modifier.isPrivate(targetType.getModifiers()))
            throw new IllegalArgumentException("classes to be proxied must not be private: " + targetType.getName());
        if (isInnerClass(targetType) && !Modifier.isStatic(targetType.getModifiers()))
            throw new IllegalArgumentException(
                    "inner classes can not be proxied. Use nested classes, i.e. make them static: "
                            + targetType.getName());
        try {
            return classPool.get(targetType.getName());
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Is this an non-static member class?
     * 
     * @see Class#getEnclosingClass()
     */
    private boolean isInnerClass(Class<T> targetType) {
        return targetType.getEnclosingClass() != null && !targetType.isAnonymousClass();
    }

    public T newInstance() {
        String className = targetType.getName() + "$$InvocationProxy";
        try {
            Class<T> type = proxyType(className);
            log.debug("instantiate {}", type);
            Constructor<T> constructor = type.getConstructor(InvocationProxy.class);
            return constructor.newInstance(this);
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
        // for debugging uncomment: proxyType.debugWriteFile("target/generated-proxies");
        return proxyType.toClass();
    }

    private void generate(String className) throws CannotCompileException, NotFoundException {
        log.debug("proxy class {}", targetType.getName());
        proxyType = classPool.makeClass(className);
        proxyType.getClassFile().setVersionToJava5();

        setSuper();

        addProxyField();
        addConstuctor();
        forwardMethods();
    }

    private void setSuper() throws CannotCompileException, NotFoundException {
        if (isAnonymousTarget()) {
            proxyType.addInterface(targetType.getInterfaces()[0]); // must be exactly one
        } else {
            proxyType.setSuperclass(targetType);
        }
    }

    private boolean isAnonymousTarget() {
        return targetType.getName().matches(".*\\$[0-9]+$");
    }

    private void addProxyField() throws CannotCompileException, NotFoundException {
        CtClass type = classPool.get(InvocationProxy.class.getName());
        CtField field = new CtField(type, "proxy", proxyType);
        field.setModifiers(Modifier.PRIVATE | Modifier.FINAL);
        try {
            field.setGenericSignature(encode(type) + "<" + encode(targetType) + ";>;");
        } catch (NoSuchMethodError e) {
            System.out.println("-----> " + CtClass.class.getPackage().getSpecificationVersion());
            throw e;
        }
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
            log.debug("proxy method {}", targetMethod.getLongName());
            CtClass returnType = targetMethod.getReturnType();
            CtMethod method = new CtMethod(returnType, targetMethod.getName(), parameterTypes, proxyType);
            StringBuilder body = new StringBuilder("{\n");
            body.append("java.lang.reflect.Method method = " + targetType.getName() + ".class.getMethod(\""
                    + targetMethod.getName() + "\", " + argTypes(parameterTypes) + ");\n");
            // + "System.out.println(\":::: \" + method);" //
            if (returnType != CtClass.voidType)
                body.append("return (" + returnType.getName() + ") ");
            body.append("proxy.invoke(method, " + args(parameterTypes.length) + ");\n");
            body.append("}");
            log.debug("method body:\n{}", body);
            method.setBody(body.toString());
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
        if (parameterTypes.length == 0)
            return "new Class[0]";
        StringBuilder types = new StringBuilder();
        for (CtClass parameterType : parameterTypes) {
            if (types.length() > 0)
                types.append(", ");
            types.append(parameterType.getName() + ".class");
        }
        return "new Class[]{" + types.toString() + "}";
    }

    private String args(int count) {
        if (count == 0)
            return "new Object[0]";
        StringBuilder args = new StringBuilder();
        for (int i = 1; i <= count; i++) {
            if (i > 1)
                args.append(", ");
            args.append("$").append(i);
        }
        return "new Object[]{" + args + "}";
    }

    public abstract Object invoke(Method method, Object... args);
}
