package net.java.messageapi.adapter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the portable CDI extension that forwards the events to the scanners that do the actual work.
 */
public class MessageApiCdiExtension implements Extension {
    private final Logger log = LoggerFactory.getLogger(MessageApiCdiExtension.class);

    private final MessageApiInterfaceScanner messageApiScanner = new MessageApiInterfaceScanner();
    private final MessageApiMdbScanner mdbScanner = new MessageApiMdbScanner();
    private final MessageApiEventScanner eventScanner = new MessageApiEventScanner();

    <X> void step1(@Observes ProcessAnnotatedType<X> pat) {
        messageApiScanner.discoverMessageApis(pat);
        mdbScanner.handleMessageApiImplementations(pat);
        eventScanner.discoverMessageEvents(pat);
    }

    void step2(@Observes ProcessInjectionTarget<?> pit) {
        for (InjectionPoint injectionPoint : pit.getInjectionTarget().getInjectionPoints()) {
            log.debug("scan injection point {}", injectionPoint);
            Class<?> type = getType(injectionPoint);
            messageApiScanner.discoverMessageApiInjectionPoint(injectionPoint, type);
            eventScanner.handleMessageEventInjectionPoint(injectionPoint, type, pit);
        }
    }

    private Class<?> getType(InjectionPoint injectionPoint) {
        Type type = injectionPoint.getType();
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            final Type[] typeArguments = parameterizedType.getActualTypeArguments();
            if (typeArguments.length == 1) {
                type = typeArguments[0];
                log.debug("parameterized type = {}", type);
            }
        }
        return (type instanceof Class) ? (Class<?>) type : null;
    }

    void step3(@Observes AfterBeanDiscovery abd) {
        messageApiScanner.createBeans(abd);
        eventScanner.createBeans(abd);
    }
}
