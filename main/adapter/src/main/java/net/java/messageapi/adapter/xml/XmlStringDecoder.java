package net.java.messageapi.adapter.xml;

import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.List;

import javax.xml.bind.*;

import net.java.messageapi.MessageApi;
import net.java.messageapi.adapter.PojoInvoker;
import net.java.messageapi.reflection.ReflectionAdapter;

import com.google.common.collect.Lists;

/**
 * Takes XML Strings, deserializes them and calls the corresponding methods in an implementation of
 * some {@link MessageApi}.
 * 
 * @param <T>
 *            the {@link MessageApi} interface that the calls are for and the <code>impl</code>
 *            implements.
 */
public class XmlStringDecoder<T> {

    public static <T> XmlStringDecoder<T> create(Class<T> api, T impl) {
        return create(api, impl, JaxbProvider.UNCHANGED);
    }

    public static <T> XmlStringDecoder<T> create(Class<T> api, T impl, JaxbProvider jaxbProvider) {
        return new XmlStringDecoder<T>(api, impl, jaxbProvider);
    }

    private final PojoInvoker<T> invoker;
    private final Unmarshaller unmarshaller;

    private XmlStringDecoder(Class<T> api, T impl, JaxbProvider jaxbProvider) {
        if (jaxbProvider == null)
            throw new NullPointerException(
                    "jaxbProvider must not be null; eventually pass JaxbProvider.UNCHANGED");
        this.invoker = new PojoInvoker<T>(api, impl);
        this.unmarshaller = createUnmarshaller(api, jaxbProvider);
        // TODO verify by calling unmarshaller.setSchema(...)
    }

    private Unmarshaller createUnmarshaller(Class<T> api, JaxbProvider jaxbProvider) {
        JAXBContext context = getContext(api, jaxbProvider);
        try {
            return context.createUnmarshaller();
        } catch (JAXBException e) {
            throw new RuntimeException("can't unmarshal", e);
        }
    }

    protected JAXBContext getContext(Class<T> api, JaxbProvider jaxbProvider) {
        List<Class<?>> classes = Lists.newArrayList();
        for (Method method : api.getMethods()) {
            ReflectionAdapter<Method> reflectionAdapter = ReflectionAdapter.of(method);
            try {
                String methodAsClassName = reflectionAdapter.getMethodNameAsClassName();
                String containerName = method.getDeclaringClass().getName();
                Class<?> type = Class.forName(containerName + "$" + methodAsClassName);
                classes.add(type);
            } catch (ClassNotFoundException e2) {
                throw new RuntimeException(e2);
            }
        }
        Class<?>[] classesToBeBound = classes.toArray(new Class[classes.size()]);
        return jaxbProvider.createJaxbContextFor(classesToBeBound);
    }

    public void decode(String xml) {
        Reader reader = new StringReader(xml);
        Object pojo = readPojo(reader);
        invoker.invoke(pojo);
    }

    private Object readPojo(Reader reader) {
        try {
            return unmarshaller.unmarshal(reader);
        } catch (JAXBException e) {
            throw new RuntimeException("can't unmarshal", e);
        }
    }
}
