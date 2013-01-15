package net.java.messageapi.adapter;

import java.lang.reflect.Type;
import java.util.*;

import javax.enterprise.inject.spi.*;
import javax.enterprise.util.AnnotationLiteral;

import net.java.messageapi.*;

import org.slf4j.*;

/**
 * Scans all implementations of {@link MessageApi} interfaces, qualifies them as {@link JmsIncoming} (so they don't
 * collide with the {@link MessageSender} that's generated for sending the message).
 */
class MessageApiMdbScanner {
    private final Logger log = LoggerFactory.getLogger(MessageApiMdbScanner.class);

    <X> void handleMessageApiImplementations(ProcessAnnotatedType<X> pat) {
        AnnotatedType<X> annotatedType = pat.getAnnotatedType();
        Set<Type> implementedMessageApis = getImplementedMessageApis(annotatedType);
        if (!implementedMessageApis.isEmpty()) {
            // TODO only if it's not already qualified
            log.info("Marking {} as JmsIncoming, as it's a receiver for message api {}", annotatedType.getJavaClass(),
                    implementedMessageApis);
            AnnotatedType<X> wrapped = new AnnotatedTypeAnnotationsWrapper<X>(annotatedType,
                    new AnnotationLiteral<JmsIncoming>() {
                        private static final long serialVersionUID = 1L;
                    });
            pat.setAnnotatedType(wrapped);
        }
    }

    /**
     * We can't simply look for all {@link MessageApiInterfaceScanner#messageApis}, as the implementing type may be
     * scanned before the {@link MessageApi} is.
     */
    private Set<Type> getImplementedMessageApis(AnnotatedType<?> type) {
        if (type.getJavaClass().isInterface())
            return Collections.emptySet();
        Set<Type> result = new HashSet<Type>();
        for (Type implementedType : type.getTypeClosure()) {
            if (isMessageApi(implementedType)) {
                result.add(implementedType);
            }
        }
        return result;
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
}
