package net.java.messageapi.adapter;

import java.lang.reflect.Type;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.*;
import javax.inject.Singleton;

import net.java.messageapi.MessageApi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageSenderCdiExtension implements Extension {
    private final Logger log = LoggerFactory.getLogger(MessageSenderCdiExtension.class);

    void afterBeanDiscovery(@Observes AfterBeanDiscovery event, BeanManager manager) {
        for (Bean<?> bean : manager.getBeans(Object.class)) {
            for (InjectionPoint injectionPoint : bean.getInjectionPoints()) {
                if (isMessageApi(injectionPoint)) {
                    inject(event, injectionPoint);
                }
            }
        }
    }

    private boolean isMessageApi(InjectionPoint injectionPoint) {
        Type type = injectionPoint.getType();
        if (!(type instanceof Class))
            return false;
        Class<?> cls = (Class<?>) type;
        return cls.isInterface() && cls.isAnnotationPresent(MessageApi.class);
    }

    private void inject(AfterBeanDiscovery event, InjectionPoint injectionPoint) {
        @SuppressWarnings("unchecked")
        final Class<Object> api = (Class<Object>) injectionPoint.getType();
        log.debug("inject {} into {}", api, injectionPoint);
        event.addBean(new AbstractBean<Object>(api, Singleton.class) {
            @Override
            public Object create(CreationalContext<Object> context) {
                return MessageSender.of(api);
            }
        });
    }
}
