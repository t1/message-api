package net.java.messageapi.reflection;

import java.lang.reflect.Method;
import java.util.List;

import javax.lang.model.element.*;

/**
 * Adapt the java reflection api or the apt api to the level of abstraction we need to generate
 * pojos.
 */
public abstract class ReflectionAdapter<T> {

    public static ReflectionAdapter<Method> of(Method method) {
        return new JavaReflectionAdapter(method);
    }

    public static ReflectionAdapter<ExecutableElement> of(ExecutableElement method) {
        return new AptReflectionAdapter(method);
    }

    public static PackageElement getPackageOf(Element element) {
        return AptReflectionAdapter.getPackageOf(element);
    }

    protected final T method;
    private final boolean unique;

    protected ReflectionAdapter(T method) {
        this.method = method;
        this.unique = initUnique(method);
    }

    private boolean initUnique(T method) {
        final String methodName = getMethodName(method);
        for (T sibling : siblings(method)) {
            if (sibling.equals(method))
                continue;
            if (methodName.equals(getMethodName(sibling))) {
                return false;
            }
        }
        return true;
    }

    protected abstract String getMethodName(T method);

    protected abstract Iterable<T> siblings(T method);

    public abstract String getPackage();

    public abstract String getDeclaringType();

    /** Is this method name unique within the type it is enclosed in? */
    public boolean isUnique() {
        return unique;
    }

    public String getMethodNameAsFullyQualifiedClassName() {
        return getDeclaringType() + "$" + getMethodNameAsClassName();
    }

    public String getMethodNameAsClassName() {
        StringBuilder methodName = new StringBuilder();
        methodName.append(getMethodName());
        methodName.setCharAt(0, Character.toUpperCase(methodName.charAt(0)));
        return methodName.toString();
    }

    public String getMethodName() {
        StringBuilder name = new StringBuilder(getMethodName(method));

        if (!unique) {
            for (Object parameter : getParameters()) {
                appendParameterExtension(name, parameter);
            }
        }
        return name.toString();
    }

    protected abstract List<?> getParameters();

    private void appendParameterExtension(StringBuilder name, Object parameter) {
        String typeName = typeNameOf(parameter);
        TypeMatcher typeMatcher = new TypeMatcher(typeName);
        if (typeMatcher.requiresImportFor(getPackage())) {
            for (String pathElement : typeName.split("\\.")) {
                name.append(Character.toUpperCase(pathElement.charAt(0)));
                name.append(pathElement.substring(1));
            }
        } else {
            name.append(typeMatcher.getRawType());
        }
    }

    protected abstract String typeNameOf(Object object);
}
