package net.java.messageapi.reflection;

import java.lang.reflect.*;
import java.lang.reflect.Modifier;

import javassist.*;
import javassist.bytecode.*;

/**
 * Read the debug info from the class file of the method, if the class was compiled with debug information and is not an
 * interface.
 * <p>
 * This class must remain separate from {@link Parameter}, so that class can run, even when javassist is not on the
 * classpath.
 */
public class DebugInfoParameterNameSupplier implements ParameterNameSupplier {
    private final ParameterNameSupplier delegate;

    public DebugInfoParameterNameSupplier(ParameterNameSupplier delegate) {
        this.delegate = delegate;
    }

    @Override
    public String get(Parameter parameter) {
        try {
            String parameterName = getParameterNameOrThrow(parameter);
            if (parameterName == null)
                return delegate.get(parameter);
            return parameterName;
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getParameterNameOrThrow(Parameter parameter) throws NotFoundException {
        Method method = parameter.getMethod();
        int index = parameter.getIndex();

        checkIndex(method, index);

        LocalVariableAttribute localVariables = getLocalVariableTable(method);
        if (localVariables == null)
            return null;
        return localVariables.variableName(index + thisOffset(method));
    }

    private static void checkIndex(Method method, int index) {
        if (index < 0)
            throw new RuntimeException("invalid negative method parameter index: " + index);
        int numberOfParameters = method.getParameterTypes().length;
        if (index >= numberOfParameters)
            throw new RuntimeException("invalid method parameter index [" + index + "] for method [" + method
                    + "] which has [" + numberOfParameters + "] parameters.");
    }

    private static LocalVariableAttribute getLocalVariableTable(Method method) throws NotFoundException {
        ClassPool classPool = new ClassPool(true);
        classPool.insertClassPath(new LoaderClassPath(method.getDeclaringClass().getClassLoader()));
        CtClass ctClass = classPool.get(method.getDeclaringClass().getName());

        CtMethod ctMethod = ctClass.getDeclaredMethod(method.getName());
        CodeAttribute code = (CodeAttribute) ctMethod.getMethodInfo().getAttribute("Code");
        // TODO if it's not an interface: log a warning: missing debug information; once per jar only!
        if (code == null)
            return null;
        return (LocalVariableAttribute) code.getAttribute("LocalVariableTable");
    }

    /** if the method is not static, the first local variable is "this" */
    private static int thisOffset(Method method) {
        return (Modifier.isStatic(method.getModifiers())) ? 0 : 1;
    }
}
