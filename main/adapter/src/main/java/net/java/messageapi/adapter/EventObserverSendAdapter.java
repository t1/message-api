package net.java.messageapi.adapter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Set;

import javax.enterprise.event.Reception;
import javax.enterprise.event.TransactionPhase;
import javax.enterprise.inject.spi.ObserverMethod;
import javax.enterprise.util.AnnotationLiteral;

import net.java.messageapi.DynamicDestinationName;
import net.java.messageapi.JmsOutgoing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;

public class EventObserverSendAdapter<T> implements ObserverMethod<T> {

    static final AnnotationLiteral<JmsOutgoing> OUTGOING = new AnnotationLiteral<JmsOutgoing>() {
        private static final long serialVersionUID = 1L;
    };
    private static final ImmutableSet<Annotation> QUALIFIERS = ImmutableSet.<Annotation> of(OUTGOING);

    private final Class<T> eventType;
    private final Logger logger;
    private final JmsSender sender;

    public EventObserverSendAdapter(Class<T> eventType) {
        this.eventType = eventType;
        this.logger = LoggerFactory.getLogger(eventType);

        this.sender = createSender();
    }

    private JmsSender createSender() {
        // TODO add jms properties
        // TODO read other configs
        JmsQueueConfig config = MessageSender.getDefaultConfig(eventType);
        // TODO allow other payload handlers
        XmlJmsPayloadHandler payloadHandler = new XmlJmsPayloadHandler();
        // TODO join with EventDecoder
        Function<Object, String> destinationNameFunction = new Function<Object, String>() {
            @Override
            public String apply(Object from) {
                for (Field field : from.getClass().getDeclaredFields()) {
                    if (field.isAnnotationPresent(DynamicDestinationName.class)) {
                        field.setAccessible(true);
                        try {
                            return (String) field.get(from);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                return null;
            }
        };
        return new JmsSender(config, payloadHandler, logger, destinationNameFunction);
    }

    @Override
    public Class<?> getBeanClass() {
        return EventObserverSendAdapter.class; // just something, why is CDI interested, anyway?!?
    }

    @Override
    public Set<Annotation> getObservedQualifiers() {
        return QUALIFIERS;
    }

    @Override
    public Type getObservedType() {
        return eventType;
    }

    @Override
    public Reception getReception() {
        return Reception.ALWAYS;
    }

    @Override
    public TransactionPhase getTransactionPhase() {
        return TransactionPhase.IN_PROGRESS;
    }

    @Override
    public void notify(T event) {
        sender.sendJms(event);
    }
}
