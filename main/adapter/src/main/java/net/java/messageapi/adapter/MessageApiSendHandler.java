package net.java.messageapi.adapter;

import java.io.Reader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import javax.interceptor.InvocationContext;

import net.java.messageapi.DestinationName;
import net.java.messageapi.MessageApiSendDelegate;
import net.java.messageapi.adapter.xml.XmlJmsPayloadHandler;

public class MessageApiSendHandler implements MessageApiSendDelegate {
    public void handle(InvocationContext ctx) {
        Class<?> api = ctx.getMethod().getDeclaringClass();

        // defaults
        String destinationName = api.getCanonicalName();

        // annotations
        for (Annotation annotation : getAnnotations(ctx.getTarget().getClass(), api)) {
            if (annotation instanceof DestinationName) {
                destinationName = ((DestinationName) annotation).value();
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

    Annotation[] getAnnotations(Class<?> targetClass, Class<?> requiredType) {
        for (Field field : targetClass.getDeclaredFields()) {
            if (field.getType().isAssignableFrom(requiredType)) {
                return field.getAnnotations();
            }
        }
        Class<?> superclass = targetClass.getSuperclass();
        if (superclass == Object.class)
            throw new IllegalStateException("no field of type " + requiredType.getCanonicalName()
                    + " found.");
        return getAnnotations(superclass, requiredType);
    }
}
