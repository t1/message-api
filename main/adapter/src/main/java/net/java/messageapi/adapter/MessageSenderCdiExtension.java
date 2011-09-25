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
        AnnotatedType<X> annotatedType = pat.getAnnotatedType();
        MessageApi annotation = annotatedType.getAnnotation(MessageApi.class);
        if (annotation == null)
            return;
        Class<X> messageApi = annotatedType.getJavaClass();
        log.info("discovered message api {}", messageApi.getName());
        messageApis.put(messageApi, new HashSet<InjectionPoint>());
    }

    <X> void step2_discoverInjectionTargets(@Observes ProcessInjectionTarget<X> pit) {
        // FIXME check if a Bean can receive non-binding qualifiers... or maybe use interceptors
        final InjectionTarget<X> injectionTarget = pit.getInjectionTarget();
        Set<InjectionPoint> injectionPoints = getMessageApiInjectionPoints(injectionTarget);
        if (injectionPoints != null) {
            for (InjectionPoint injectionPoint : injectionPoints) {
                @SuppressWarnings("unchecked")
                final Class<X> api = (Class<X>) injectionPoint.getType();
                log.info("discovered {}@{}", api, injectionPoint.getBean());
                messageApis.get(api).add(injectionPoint);
            }
        }
    }

    private <X> Set<InjectionPoint> getMessageApiInjectionPoints(InjectionTarget<X> injectionTarget) {
        Set<InjectionPoint> result = null;
        for (InjectionPoint injectionPoint : injectionTarget.getInjectionPoints()) {
            Type type = injectionPoint.getType();
            // FIXME resolve Instance types
            if (Instance.class.equals(type)) {
                @SuppressWarnings("unchecked")
                Class<Instance<?>> instance = (Class<Instance<?>>) type;
                log.info("generic interfaces: {}", (Object) instance.getGenericInterfaces());
            }
            log.debug("injection point {}@{}", type, injectionPoint.getBean());
            if (messageApis.containsKey(type)) {
                if (result == null)
                    result = new HashSet<InjectionPoint>();
                result.add(injectionPoint);
            }
        }
        return result;
    }

    void step3_createBeans(@Observes AfterBeanDiscovery abd, BeanManager bm) {
        for (Entry<Class<?>, Set<InjectionPoint>> entry : messageApis.entrySet()) {
            Class<?> api = entry.getKey();
            for (InjectionPoint injectionPoint : entry.getValue()) {
                log.info("create bean for {}@{}", api, injectionPoint);
                abd.addBean(MessageApiBean.of(api, injectionPoint));
            }
        }
    }
}
