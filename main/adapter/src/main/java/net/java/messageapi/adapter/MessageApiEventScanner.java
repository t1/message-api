package net.java.messageapi.adapter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.*;

import net.java.messageapi.MessageEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

/**
 * Scans {@link MessageEvent}s and generates {@link EventObserverSendAdapter observers} to forward them to JMS.
 * 
 * TODO generate the {@link EventDecoder}-MDBs dynamically (don't know how to register them with the container)
 */
public class MessageApiEventScanner {
    private final Logger log = LoggerFactory.getLogger(MessageApiEventScanner.class);

    private final Set<Class<?>> messageEvents = Sets.newHashSet();
    private final Set<ObserverMethod<?>> observers = Sets.newHashSet();

    <X> void discoverMessageEvents(ProcessAnnotatedType<X> pat) {
        AnnotatedType<X> annotatedType = pat.getAnnotatedType();

        MessageEvent annotation = annotatedType.getAnnotation(MessageEvent.class);
        if (annotation != null) {
            Class<X> messageEvent = annotatedType.getJavaClass();
            log.info("discovered message event {}", messageEvent.getName());
            messageEvents.add(messageEvent);
        }
    }

    private Object getBeanName(InjectionPoint injectionPoint) {
        final Bean<?> bean = injectionPoint.getBean();
        return (bean == null) ? "???" : bean.getBeanClass().getSimpleName();
    }

    void handleMessageEventInjectionPoint(InjectionPoint injectionPoint, Class<?> type, ProcessInjectionTarget<?> pit) {
        if (messageEvents.contains(type)) {
            final Set<Annotation> qualifiers = injectionPoint.getQualifiers();
            log.info(
                    "discovered injection point named \"{}\" in {} for message event {} qualified as {}; "
                            + "qualifying as JmsOutgoing and generating observer",
                    new Object[] { injectionPoint.getMember().getName(), getBeanName(injectionPoint),
                            type.getSimpleName(), qualifiers });
            annotateAsOutgoing(injectionPoint, pit);
            generateOutgoingAdapter(type);
        }
    }

    private <T> void annotateAsOutgoing(final InjectionPoint injectionPoint, ProcessInjectionTarget<T> pit) {
        // TODO this doesn't work... why?
        InjectionTarget<T> target = pit.getInjectionTarget();
        log.debug("wrapping {} in {}", injectionPoint, target);
        InjectionTargetWrapper<T> wrapper = new InjectionTargetWrapper<T>(target) {
            @Override
            public Set<InjectionPoint> getInjectionPoints() {
                Set<InjectionPoint> injectionPoints = Sets.newHashSet(super.getInjectionPoints());
                boolean removed = injectionPoints.remove(injectionPoint);
                log.debug("removed {}: {}", injectionPoint, removed);
                injectionPoints.add(new InjectionPointWrapper(injectionPoint) {
                    @Override
                    public Set<Annotation> getQualifiers() {
                        Set<Annotation> qualifiers = Sets.newHashSet(super.getQualifiers());
                        qualifiers.add(EventObserverSendAdapter.OUTGOING);
                        log.debug("adding @JmsOutgoing to {} -> {}", injectionPoint, qualifiers);
                        return qualifiers;
                    }
                });
                return injectionPoints;
            }
        };
        pit.setInjectionTarget(wrapper);
    }

    private void generateOutgoingAdapter(Class<?> type) {
        EventObserverSendAdapter<?> adapter = new EventObserverSendAdapter<Object>(type);
        observers.add(adapter);
    }

    <T, X> void scanObserverMethod(@Observes ProcessObserverMethod<T, X> pom) {
        ObserverMethod<T> observerMethod = pom.getObserverMethod();
        Type observedType = observerMethod.getObservedType();
        if (messageEvents.contains(observedType)) {
            Class<?> observedClass = (Class<?>) observedType;
            log.info("found observer {} for {} qualified as {}", new Object[] { observerMethod,
                    observedClass.getName(), observerMethod.getObservedQualifiers() });
            // TODO add JmsIncoming annotation... but how?!?
        }
    }

    void createBeans(@Observes AfterBeanDiscovery abd) {
        for (ObserverMethod<?> observer : observers) {
            abd.addObserverMethod(observer);
        }
    }
}
