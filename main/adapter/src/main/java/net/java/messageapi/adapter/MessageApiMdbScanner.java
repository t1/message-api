package net.java.messageapi.adapter;

import java.lang.reflect.Type;
import java.util.Set;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.*;
import javax.enterprise.util.AnnotationLiteral;

import net.java.messageapi.JmsIncoming;
import net.java.messageapi.MessageApi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * Scans all implementations of {@link MessageApi} interfaces, qualifies them as {@link JmsIncoming} (so they don't
 * collide with the {@link MessageSender} that's generated for sending the message), and generates a
 * {@link MessageDecoder} MDB to forward the message to the implementation.
 * 
 * FIXME how can we register the MDB?
 */
class MessageApiMdbScanner {
    private final Logger log = LoggerFactory.getLogger(MessageApiMdbScanner.class);

    private final Set<Class<?>> mdbs = Sets.newHashSet();

    <X> void handleMessageApiImplementations(ProcessAnnotatedType<X> pat) {
        AnnotatedType<X> annotatedType = pat.getAnnotatedType();
        Set<Type> implementedMessageApis = getImplementedMessageApis(annotatedType);
        if (!implementedMessageApis.isEmpty()) {
            // TODO only if it's not already qualified
            AnnotatedType<X> wrapped = new AnnotatedTypeAnnotationsWrapper<X>(annotatedType,
                    new AnnotationLiteral<JmsIncoming>() {
                        private static final long serialVersionUID = 1L;
                    });
            pat.setAnnotatedType(wrapped);
            log.info("Marking {} as JmsIncoming, as it's a receiver for message api {}", annotatedType.getJavaClass(),
                    implementedMessageApis);
            // FIXME what if the bean implements multiple messageapis?
            MdbGenerator generator = new MdbGenerator((Class<?>) implementedMessageApis.iterator().next());
            if (generator.isGenerated()) {
                Class<?> mdb = generator.get();
                log.info("MDB {} was generated", mdb.getName());
                mdbs.add(mdb);
            }
        }
    }

    /**
     * We can't simply look for all {@link MessageApiInterfaceScanner#messageApis}, as the implementing type may be
     * scanned before the {@link MessageApi} is.
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
            if (implementedClass.isInterface() && implementedClass.isAnnotationPresent(MessageApi.class)) {
                return true;
            }
        }
        return false;
    }

    void createBeans(@Observes AfterBeanDiscovery abd) {
        for (Class<?> mdb : mdbs) {
            log.info("register MDB {}", mdb);
            abd.addBean(new MdbBean(mdb));
        }
    }
}
