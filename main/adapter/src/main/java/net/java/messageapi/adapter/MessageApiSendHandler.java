package net.java.messageapi.adapter;

import java.io.Reader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import javax.interceptor.InvocationContext;

import net.java.messageapi.DestinationName;
import net.java.messageapi.MessageApiSendDelegate;
import net.java.messageapi.adapter.xml.XmlJmsPayloadHandler;

public class MessageApiSendHandler implements MessageApiSendDelegate {
    /** doesn't work, yet :-(( */
    private static final boolean ANNOTATION_SCANNING = false;

    public void handle(InvocationContext ctx) {
        Class<?> api = ctx.getMethod().getDeclaringClass();

        // defaults
        String destinationName = api.getCanonicalName();

        // annotations
        if (ANNOTATION_SCANNING) {
            Class<? extends Object> targetClass = ctx.getTarget().getClass();
            for (Annotation annotation : getAnnotations(targetClass, targetClass, api)) {
                if (annotation instanceof DestinationName) {
                    destinationName = ((DestinationName) annotation).value();
                }
            }
        }

        // xml-config
        Reader reader = MessageSender.getReaderFor(api);
        if (reader != null) {
            JmsSenderFactory config = (JmsSenderFactory) MessageSender.readConfigFrom(reader, api);
            destinationName = config.getConfig().getDestinationName();
        }

        JmsQueueConfig config = new JmsQueueConfig("ConnectionFactory", destinationName, null,
                null, false, null, null);
        JmsSenderFactory factory = new JmsSenderFactory(config, new XmlJmsPayloadHandler());

        Object payload = factory.getPayloadHandler().toPayload(api, ctx.getMethod(),
                ctx.getParameters());
        factory.sendJms(api, payload);
    }

    Annotation[] getAnnotations(Class<?> targetClass, Class<?> currentClass, Class<?> requiredType) {
        for (Field field : currentClass.getDeclaredFields()) {
            if (field.getType().isAssignableFrom(requiredType)) {
                return field.getAnnotations();
            }
        }
        Class<?> superclass = currentClass.getSuperclass();
        if (superclass == Object.class)
            throw new IllegalStateException("no field of type " + requiredType.getCanonicalName()
                    + " found in " + targetClass);
        return getAnnotations(targetClass, superclass, requiredType);
    }
}
