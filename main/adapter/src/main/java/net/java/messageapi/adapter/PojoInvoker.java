package net.java.messageapi.adapter;

import java.lang.reflect.Method;
import java.util.List;

import net.java.messageapi.MessageApi;
import net.java.messageapi.reflection.Parameter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Calls methods from an implementation of an {@link MessageApi} by converting instances of the generated POJOs back to
 * method calls.
 */
public class PojoInvoker<T> {

    final Logger log = LoggerFactory.getLogger(PojoInvoker.class);

    public static <T> PojoInvoker<T> of(Class<T> api, T impl) {
        return new PojoInvoker<T>(api, impl);
    }

    /** the impl is *not* enough: it may be a proxy, so reflection would return the wrong methods */
    private final Class<T> api;
    private final T impl;

    public PojoInvoker(Class<T> api, T impl) {
        if (api == null)
            throw new RuntimeException("api must not be null");
        if (impl == null)
            throw new RuntimeException("impl must not be null");
        if (!api.isInstance(impl))
            throw new IllegalArgumentException(api.getName() + " is not implemented by " + impl.getClass());
        this.api = api;
        this.impl = impl;
    }

    public void invoke(Object pojo) {
        log.trace("invoke for {}", pojo);
        PojoProperties pojoProperties = PojoProperties.of(pojo);

        String methodName = getMethodNameFor(pojo);
        log.trace("search {} with {}", methodName, pojoProperties);

        for (Method method : api.getMethods()) {
            log.trace("compare {}", method);
            if (method.getName().equals(methodName)) {
                List<Parameter> methodParameters = Parameter.allOf(method);
                if (pojoProperties.matches(methodParameters)) {
                    log.trace("parameters match... invoke");
                    pojoProperties.invoke(impl, method, methodParameters);
                    return;
                }
            }
        }
        throw new RuntimeException("method [" + methodName + "] with properties " + pojoProperties + " not found in "
                + api);
    }

    private String getMethodNameFor(Object pojo) {
        String[] names = pojo.getClass().getSimpleName().split("\\$");
        String name = names[names.length - 1];
        return Character.toLowerCase(name.charAt(0)) + name.substring(1);
    }
}
