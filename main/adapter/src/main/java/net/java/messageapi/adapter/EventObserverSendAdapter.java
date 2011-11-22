package net.java.messageapi.adapter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;

import javax.enterprise.event.Reception;
import javax.enterprise.event.TransactionPhase;
import javax.enterprise.inject.spi.ObserverMethod;
import javax.enterprise.util.AnnotationLiteral;

import net.java.messageapi.JmsOutgoing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;

public class EventObserverSendAdapter<T> implements ObserverMethod<T> {

    static final AnnotationLiteral<JmsOutgoing> OUTGOING = new AnnotationLiteral<JmsOutgoing>() {
        private static final long serialVersionUID = 1L;
    };
    private static final ImmutableSet<Annotation> QUALIFIERS = ImmutableSet.<Annotation> of(OUTGOING);

    private final Class<?> eventType;
    private final Logger logger;

    public EventObserverSendAdapter(Class<?> eventType) {
        this.eventType = eventType;
        this.logger = LoggerFactory.getLogger(eventType);
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
        // TODO add jms properties
        // TODO read other configs
        JmsQueueConfig config = MessageSender.getDefaultConfig(eventType);
        // TODO allow other payload handlers
        XmlJmsPayloadHandler payloadHandler = new XmlJmsPayloadHandler();
        JmsSender sender = new JmsSender(config, payloadHandler, logger);
        sender.sendJms(event);
    }
}
