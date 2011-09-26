package net.java.messageapi.adapter;

import java.lang.reflect.Type;
import java.util.*;
import java.util.Map.Entry;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.*;

import net.java.messageapi.MessageApi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageSenderCdiExtension implements Extension {
    private final Logger log = LoggerFactory.getLogger(MessageSenderCdiExtension.class);

    // TODO provide the set of discovered message apis for injection
    private final Map<Class<?>, Set<InjectionPoint>> messageApis = new HashMap<Class<?>, Set<InjectionPoint>>();

    <X> void step1_discoverMessageApis(@Observes ProcessAnnotatedType<X> pat) {
        discoverMessageApi(pat);
        vetoMessageApiImplementations(pat);
    }

    private <X> void discoverMessageApi(ProcessAnnotatedType<X> pat) {
        AnnotatedType<X> annotatedType = pat.getAnnotatedType();

        MessageApi annotation = annotatedType.getAnnotation(MessageApi.class);
        if (annotation != null) {
            Class<X> messageApi = annotatedType.getJavaClass();
            log.info("discovered message api {}", messageApi.getName());
            messageApis.put(messageApi, new HashSet<InjectionPoint>());
        }
    }

    private <X> void vetoMessageApiImplementations(ProcessAnnotatedType<X> pat) {
        AnnotatedType<X> annotatedType = pat.getAnnotatedType();
        final Set<Type> typeClosure = annotatedType.getTypeClosure();
        if (!isInterface(annotatedType) && intersect(typeClosure, messageApis.keySet())) {
            pat.veto();
            log.info(
                    "Preventing {} from being installed as bean, as it's a receiver for a message api",
                    annotatedType.getJavaClass());
        }
    }

    private <X> boolean isInterface(AnnotatedType<X> annotatedType) {
        return annotatedType.getJavaClass().isInterface();
    }

    private boolean intersect(Set<?> setA, Set<?> setB) {
        for (Object elementOfA : setA) {
            if (setB.contains(elementOfA)) {
                return true;
            }
        }
        return false;
    }

    <X> void step2_discoverInjectionTargets(@Observes ProcessInjectionTarget<X> pit) {
        // FIXME check if a Bean can receive non-binding qualifiers... or maybe use interceptors
        final InjectionTarget<X> injectionTarget = pit.getInjectionTarget();
        for (InjectionPoint injectionPoint : getMessageApiInjectionPoints(injectionTarget)) {
            final Class<X> api = getMessageApi(injectionPoint);
            log.info("add injection point {} for message api {}", injectionPoint.getBean(),
                    api.getName());
            messageApis.get(api).add(injectionPoint);
        }
    }

    private <X> Class<X> getMessageApi(InjectionPoint injectionPoint) {
        @SuppressWarnings("unchecked")
        final Class<X> api = (Class<X>) injectionPoint.getType();
        return api;
    }

    private <X> Set<InjectionPoint> getMessageApiInjectionPoints(InjectionTarget<X> injectionTarget) {
        final Set<InjectionPoint> result = new HashSet<InjectionPoint>();
        for (InjectionPoint injectionPoint : injectionTarget.getInjectionPoints()) {
            Type type = injectionPoint.getType();
            if (Instance.class.equals(type)) {
                @SuppressWarnings("unchecked")
                Class<Instance<?>> instance = (Class<Instance<?>>) type;
                // FIXME resolve Instance type
                log.info("generic interfaces: {}", (Object) instance.getGenericInterfaces());
            }
            if (messageApis.containsKey(type)) {
                String apiName = getMessageApi(injectionPoint).getName();
                Bean<?> bean = injectionPoint.getBean();
                log.debug("discovered injection point for message api {} in {}", apiName, bean);
                result.add(injectionPoint);
            }
        }
        return result;
    }

    void step3_createBeans(@Observes AfterBeanDiscovery abd, BeanManager bm) {
        log.info("create beans for {} message api", messageApis.size());
        for (Entry<Class<?>, Set<InjectionPoint>> entry : messageApis.entrySet()) {
            Class<?> api = entry.getKey();
            log.info("create {} beans for {}", entry.getValue().size(), api.getName());
            for (InjectionPoint injectionPoint : entry.getValue()) {
                log.info("create bean for {}", injectionPoint);
                abd.addBean(MessageApiBean.of(api, injectionPoint));
            }
        }
    }
}
