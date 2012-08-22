package net.java.messageapi.adapter;

import java.lang.annotation.Annotation;
import java.util.Set;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.*;

import net.java.messageapi.MessageApi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

/**
 * Scans all interfaces annotated as {@link MessageApi} and all injection points for them; then it creates
 * {@link MessageApiBean}s matching the qualifiers on the injection points. If several injection points have the same
 * annotations, they all can share the same bean.
 */
class MessageApiInterfaceScanner {
    /** If there are lots of beans, it's useful to log the sum. */
    private static final int BEANCOUNT_SUM_LOG_THRESHOLD = 5;

    private static final VersionSupplier versionSupplier = new VersionSupplier();

    private final Logger log = LoggerFactory.getLogger(MessageApiInterfaceScanner.class);

    private final Set<Class<?>> messageApis = Sets.newHashSet();
    private final Set<BeanId> beanIds = Sets.newHashSet();

    <X> void discoverMessageApis(ProcessAnnotatedType<X> pat) {
        AnnotatedType<X> annotatedType = pat.getAnnotatedType();

        MessageApi annotation = annotatedType.getAnnotation(MessageApi.class);
        if (annotation != null) {
            Class<X> messageApi = annotatedType.getJavaClass();
            String version = versionSupplier.getVersion(messageApi);
            log.info("discovered message api {} version {}", messageApi.getName(), //
                    (version == null) ? "unknown" : version);
            messageApis.add(messageApi);
        }
    }

    void discoverMessageApiInjectionPoint(InjectionPoint injectionPoint, Class<?> type) {
        if (messageApis.contains(type)) {
            final Set<Annotation> qualifiers = injectionPoint.getQualifiers();
            log.info(
                    "discovered injection point named \"{}\" in {} for message api {} qualified as {}",
                    new Object[] { injectionPoint.getMember().getName(), getBeanName(injectionPoint),
                            type.getSimpleName(), qualifiers });
            BeanId beanId = new BeanId(type, qualifiers);
            boolean added = beanIds.add(beanId);
            if (!added) {
                log.info("bean {} already defined", beanId);
            }
        }
    }

    private Object getBeanName(InjectionPoint injectionPoint) {
        final Bean<?> bean = injectionPoint.getBean();
        return (bean == null) ? "???" : bean.getBeanClass().getSimpleName();
    }

    void createBeans(@Observes AfterBeanDiscovery abd) {
        if (beanIds.size() > BEANCOUNT_SUM_LOG_THRESHOLD)
            log.info("define {} beans for {} message apis", beanIds.size(), messageApis.size());
        for (BeanId beanId : beanIds) {
            log.info("define message api bean for {}", beanId);
            MessageApiBean<?> bean = MessageApiBean.of(beanId.type, beanId.qualifiers);
            abd.addBean(bean);
        }
    }
}
