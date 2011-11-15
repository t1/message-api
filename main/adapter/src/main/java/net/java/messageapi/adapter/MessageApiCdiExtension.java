package net.java.messageapi.adapter;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Set;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.*;
import javax.enterprise.util.AnnotationLiteral;

import net.java.messageapi.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class MessageApiCdiExtension implements Extension {
    private final Logger log = LoggerFactory.getLogger(MessageApiCdiExtension.class);

    // TODO provide the set of discovered message apis for injection

    private final Set<Class<?>> messageApis = Sets.newHashSet();
    private final Set<Class<?>> messageEvents = Sets.newHashSet();
    private final Set<Class<?>> mdbs = Sets.newHashSet();
    private final Set<BeanId> beanIds = Sets.newHashSet();

    <X> void step1_discoverMessageApis(@Observes ProcessAnnotatedType<X> pat) {
        discoverMessageApis(pat);
        handleMessageApiImplementations(pat);
        discoverMessageEvents(pat);
    }

    private <X> void discoverMessageApis(ProcessAnnotatedType<X> pat) {
        AnnotatedType<X> annotatedType = pat.getAnnotatedType();

        MessageApi annotation = annotatedType.getAnnotation(MessageApi.class);
        if (annotation != null) {
            Class<X> messageApi = annotatedType.getJavaClass();
            log.info("discovered message api {}", messageApi.getName());
            messageApis.add(messageApi);
        }
    }

    private <X> void handleMessageApiImplementations(ProcessAnnotatedType<X> pat) {
        AnnotatedType<X> annotatedType = pat.getAnnotatedType();
        Set<Type> implementedMessageApis = getImplementedMessageApis(annotatedType);
        if (!implementedMessageApis.isEmpty()) {
            // TODO only if it's not already annotated
            AnnotatedType<X> wrapped = new AnnotatedTypeAnnotationsWrapper<X>(annotatedType,
                    new AnnotationLiteral<JmsReceiver>() {
                        private static final long serialVersionUID = 1L;
                    });
            pat.setAnnotatedType(wrapped);
            log.info("Marking {} as JmsReceiver, as it's a receiver for message api {}",
                    annotatedType.getJavaClass(), implementedMessageApis);
            // FIXME what if the bean implements multiple messageapis?
            // FIXME how can we register the MDB?
            MdbGenerator generator = new MdbGenerator(
                    (Class<?>) implementedMessageApis.iterator().next());
            if (generator.isGenerated()) {
                Class<?> mdb = generator.get();
                log.info("MDB {} was generated", mdb.getName());
                mdbs.add(mdb);
            }
        }
    }

    private <X> void discoverMessageEvents(ProcessAnnotatedType<X> pat) {
        AnnotatedType<X> annotatedType = pat.getAnnotatedType();

        MessageEvent annotation = annotatedType.getAnnotation(MessageEvent.class);
        if (annotation != null) {
            Class<X> messageEvent = annotatedType.getJavaClass();
            log.info("discovered message event {}", messageEvent.getName());
            messageEvents.add(messageEvent);
        }
    }

    /**
     * We can't simply look for all {@link #messageApis}, as the implementing type may be scanned
     * before the {@link MessageApi} is.
     */
    private Set<Type> getImplementedMessageApis(AnnotatedType<?> type) {
        if (type.getJavaClass().isInterface())
            return ImmutableSet.of();
        ImmutableSet.Builder<Type> result = ImmutableSet.builder();
        for (Type implementedType : type.getTypeClosure()) {
            if (isMessageApi(implementedType)) {
                result.add(implementedType);
            }
        }
        return result.build();
    }

    private boolean isMessageApi(Type implementedType) {
        if (implementedType instanceof Class) {
            Class<?> implementedClass = (Class<?>) implementedType;
            if (implementedClass.isInterface()
                    && implementedClass.isAnnotationPresent(MessageApi.class)) {
                return true;
            }
        }
        return false;
    }

    void step2_discoverInjectionTargets(@Observes ProcessInjectionTarget<?> pit) {
        for (InjectionPoint injectionPoint : pit.getInjectionTarget().getInjectionPoints()) {
            log.debug("scan injection point {}", injectionPoint);
            Class<?> type = getMessageApi(injectionPoint);
            if (messageApis.contains(type)) {
                final Set<Annotation> qualifiers = injectionPoint.getQualifiers();
                log.info(
                        "discovered injection point named \"{}\" in {} for message api {} qualified as {}",
                        new Object[] { injectionPoint.getMember().getName(),
                                getBeanName(injectionPoint), type.getSimpleName(), qualifiers });
                BeanId beanId = new BeanId(type, qualifiers);
                boolean added = beanIds.add(beanId);
                if (!added) {
                    log.info("bean {} already defined", beanId);
                }
            }
        }
    }

    private Class<?> getMessageApi(InjectionPoint injectionPoint) {
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

    private Object getBeanName(InjectionPoint injectionPoint) {
        final Bean<?> bean = injectionPoint.getBean();
        return (bean == null) ? "???" : bean.getBeanClass().getSimpleName();
    }

    <T, X> void scanObserverMethod(@Observes ProcessObserverMethod<T, X> pom) {
        ObserverMethod<T> observerMethod = pom.getObserverMethod();
        Type observedType = observerMethod.getObservedType();
        if (messageEvents.contains(observedType)) {
            Class<?> observedClass = (Class<?>) observedType;
            log.info("found observer {} for {}", observerMethod, observedClass.getName());
            // TODO add JmsReceiver annotation... but how?!?
        }
    }

    void step3_createBeans(@Observes AfterBeanDiscovery abd) {
        log.info("create {} beans for {} message apis", beanIds.size(), messageApis.size());
        for (BeanId beanId : beanIds) {
            log.info("create bean for {}", beanId);
            MessageApiBean<?> bean = MessageApiBean.of(beanId.type, beanId.qualifiers);
            abd.addBean(bean);
        }
        for (Class<?> mdb : mdbs) {
            log.info("register MDB {}", mdb);
            abd.addBean(new MdbBean(mdb));
        }
    }
}
