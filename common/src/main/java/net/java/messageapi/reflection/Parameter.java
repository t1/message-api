package net.java.messageapi.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

/**
 * The Java reflection api regards method arguments as second class citizens: They are not
 * represented as objects like Class, Method, Package, etc. but only accessible through helper
 * methods... and the parameter name is not available. It may be stored in the debug infos, but
 * those are not accessible through the normal apis. This class tries to fill that gap as good as it
 * goes.
 * <p>
 * To get the names of the parameters, store them into a file with the same name as the class or
 * interface that the method is in plus <code>.parametermap</code>. The file simply contains the
 * full signatures of all methods. If there is no such file, {@link #getName()} will throw a
 * {@link NoParameterMapFileException}.
 * 
 * @see net.sf.twip.util.Parameter
 */
public class Parameter {

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

    public String getName() throws NoParameterMapFileException, InvalidParameterMapFileException {
        if (name == null) {
            // TODO cache the parsers
            name = new ParameterMapParser(method).getParameterName(index);
        }
        return name;
    }

    /**
     * Can that value be passed as this parameter? I.e. is it an instance of the correct type or (if
     * it's not really primitive) <code>null</code>; correctly handles primitive types where
     * {@link Class#isInstance(Object)} returns <code>false</code> for.
     */
    public boolean isCallable(Object value) {
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
