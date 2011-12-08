package net.java.messageapi.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

/**
 * The Java reflection api regards method arguments as second class citizens: They are not
 * represented as objects like Class, Method, Package, etc. are. They are only accessible through
 * helper methods. This class tries to fill that gap as good as it goes.
 * 
 * Note that the parameter name is not accessible through the normal reflection apis. It is stored
 * in the debug infos, though, so we use javassist to read it. If your code is not compiled with
 * debug options, if the method is abstract (including interfaces), or if javassist is not
 * available, we'll fall back to the generic name <code>arg0</code> etc.
 */
public class Parameter {

    private static final ParameterNameSupplier PARAMETER_NAME_SUPPLIER = parameterNameSupplierStack();

    private static ParameterNameSupplier parameterNameSupplierStack() {
        ParameterNameSupplier stack = new FallbackParameterNameSupplier();
        if (javassistAvailable())
            stack = new DebugInfoParameterNameSupplier(stack);
        stack = new ParameterMapNameSupplier(stack);
        return stack;
    }

    private static boolean javassistAvailable() {
        try {
            Class.forName("javassist.ClassPool");
        } catch (ClassNotFoundException e) {
            return false;
        }
        return true;
    }

    public static List<Parameter> allOf(Method method) {
        final List<Parameter> list = new ArrayList<Parameter>();
        for (int i = 0; i < method.getParameterTypes().length; i++) {
            list.add(new Parameter(method, i));
        }
        return Collections.unmodifiableList(list);
    }

    private final Method method;

    private final int index;

    private String name = null; // lazy init

    public Parameter(Method method, int index) {
        this.method = method;
        this.index = index;
    }

    public <T extends Annotation> boolean isAnnotationPresent(Class<T> annotationType) {
        return getAnnotation(annotationType) != null;
    }

    /**
     * @return the annotation of that type or <code>null</code> if the parameter is not annotated
     *         with that type.
     */
    public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
        for (final Annotation annotation : getAnnotations()) {
            if (annotationType.isInstance(annotation)) {
                return annotationType.cast(annotation);
            }
        }
        return null;
    }

    public Annotation[] getAnnotations() {
        return method.getParameterAnnotations()[index];
    }

    public int getIndex() {
        return index;
    }

    public Method getMethod() {
        return method;
    }

    public Type getGenericType() {
        return method.getGenericParameterTypes()[index];
    }

    public Class<?> getType() {
        return method.getParameterTypes()[index];
    }

    public String getName() {
        if (name == null) {
            name = PARAMETER_NAME_SUPPLIER.get(method, index);
        }
        return name;
    }

    /**
     * Can that value be passed as this parameter? I.e. is it an instance of the correct type or (if
     * it's not really primitive) <code>null</code>; correctly handles primitive types where
     * {@link Class#isInstance(Object)} returns <code>false</code> for.
     */
    public boolean isAssignable(Object value) {
        Class<?> parameterType = getType();
        if (parameterType.isPrimitive()) {
            if (parameterType == Boolean.TYPE)
                return value.getClass() == Boolean.class;
            if (parameterType == Byte.TYPE)
                return value.getClass() == Byte.class;
            if (parameterType == Character.TYPE)
                return value.getClass() == Character.class;
            if (parameterType == Short.TYPE)
                return value.getClass() == Short.class;
            if (parameterType == Integer.TYPE)
                return value.getClass() == Integer.class;
            if (parameterType == Long.TYPE)
                return value.getClass() == Long.class;
            if (parameterType == Float.TYPE)
                return value.getClass() == Float.class;
            if (parameterType == Double.TYPE)
                return value.getClass() == Double.class;
            throw new AssertionError("unsupported primitive type: " + parameterType);
        } else {
            return value == null || parameterType.isInstance(value);
        }
    }

    @Override
    public String toString() {
        return Parameter.class.getSimpleName() + "#" + getIndex()
                + ((name == null) ? "" : (":" + name)) + " of " + getMethod().toGenericString();
    }
}
