package net.java.messageapi.adapter;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Set;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.*;
import javax.enterprise.util.AnnotationLiteral;

import net.java.messageapi.JmsReceiver;
import net.java.messageapi.MessageApi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class MessageApiCdiExtension implements Extension {
    private final Logger log = LoggerFactory.getLogger(MessageApiCdiExtension.class);

    // TODO provide the set of discovered message apis for injection

    private final Set<Class<?>> messageApis = Sets.newHashSet();
    private final Set<BeanId> beanIds = Sets.newHashSet();

    <X> void step1_discoverMessageApis(@Observes ProcessAnnotatedType<X> pat) {
        discoverMessageApi(pat);
        handleMessageApiImplementations(pat);
    }

    private <X> void discoverMessageApi(ProcessAnnotatedType<X> pat) {
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
            AnnotatedType<X> wrapped = new AnnotatedTypeAnnotationsWrapper<X>(annotatedType,
                    new AnnotationLiteral<JmsReceiver>() {
                        private static final long serialVersionUID = 1L;
                    });
            pat.setAnnotatedType(wrapped);
            log.info("Marking {} as JmsReceiver, as it's a receiver for message api {}",
                    annotatedType.getJavaClass(), implementedMessageApis);
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
                boolean added = beanIds.add(new BeanId(type, qualifiers));
                if (!added) {
                    log.info("bean already defined");
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

    void step3_createBeans(@Observes AfterBeanDiscovery abd) {
        log.info("create {} beans for {} message apis", beanIds.size(), messageApis.size());
        for (BeanId beanId : beanIds) {
            log.info("create bean for {}", beanId);
            MessageApiBean<?> bean = MessageApiBean.of(beanId.type, beanId.qualifiers);
            abd.addBean(bean);
        }
    }
}
