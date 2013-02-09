package net.java.messageapi.adapter;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

import javax.enterprise.event.*;
import javax.enterprise.inject.spi.ObserverMethod;
import javax.enterprise.util.AnnotationLiteral;

import net.java.messageapi.*;

import org.slf4j.*;

public class EventObserverSendAdapter<T> implements ObserverMethod<T> {

    static final AnnotationLiteral<JmsOutgoing> OUTGOING = new AnnotationLiteral<JmsOutgoing>() {
        private static final long serialVersionUID = 1L;
    };
    private static final Set<Annotation> QUALIFIERS = Collections.<Annotation> singleton(OUTGOING);

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
        DestinationNameFunction destinationNameFunction = new DestinationNameFunction() {
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
        return new JmsSender(config, payloadHandler, eventType, logger, destinationNameFunction);
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

    @Override
    public String toString() {
        return eventType + " -> " + sender;
    }
}
