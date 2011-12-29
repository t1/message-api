package net.java.messageapi.adapter;

import java.lang.annotation.Annotation;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;

import net.java.messageapi.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;

final class MessageApiBean<T> extends AbstractBean<T> {
    private final Logger log = LoggerFactory.getLogger(MessageApiBean.class);

    public static <T> MessageApiBean<T> of(Class<T> api, Annotation... qualifiers) {
        return MessageApiBean.of(api, ImmutableSet.<Annotation> of(qualifiers));
    }

    public static <T> MessageApiBean<T> of(Class<T> api, Set<Annotation> qualifiers) {
        return new MessageApiBean<T>(api, qualifiers);
    }

    private final Set<Annotation> qualifiers;
    final JmsSenderFactory factory;

    private MessageApiBean(Class<T> api, Set<Annotation> qualifiers) {
        super(api);
        this.qualifiers = qualifiers;
        this.factory = getFactory();
    }

    private JmsSenderFactory getFactory() {
        JmsQueueConfig config = new JmsQueueConfig(getConnectionFactory(), getDestinationName());
        return new JmsSenderFactory(config, getPayloadHandler());
    }

    private String getConnectionFactory() {
        ConnectionFactoryName connectionFactoryName = getQualifier(ConnectionFactoryName.class);
        if (connectionFactoryName == null)
            return ConnectionFactoryName.DEFAULT;
        return connectionFactoryName.value();
    }

    private String getDestinationName() {
        DestinationName destinationName = getQualifier(DestinationName.class);
        if (destinationName == null)
            return type.getCanonicalName();
        return destinationName.value();
    }

    private <Q> Q getQualifier(Class<Q> qualifierType) {
        for (Annotation qualifier : qualifiers) {
            if (qualifierType.isInstance(qualifier)) {
                return qualifierType.cast(qualifier);
            }
        }
        return null;
    }

    private JmsPayloadHandler getPayloadHandler() {
        if (type.isAnnotationPresent(JmsMappedPayload.class)) {
            return new MapJmsPayloadHandler(buildMapping());
        } else if (type.isAnnotationPresent(JmsSerializedPayload.class)) {
            return new SerializedJmsPayloadHandler();
        } else {
            return new XmlJmsPayloadHandler();
        }
    }

    public Mapping buildMapping() {
        MappingBuilder builder = new MappingBuilder(type);
        // JmsPayloadMapping mapping = type.getAnnotation(JmsMappedPayload.class);
        // TODO pass annotation parameters into MappingBuilder
        return builder.build();
    }

    @Override
    public String getName() {
        return super.getName() + qualifiers;
    }

    @Override
    public Set<Annotation> getQualifiers() {
        return qualifiers;
    }

    @Override
    public T create(CreationalContext<T> ctx) {
        try {
            log.info("instantiate message api bean {} qualified as {}", type.getSimpleName(), qualifiers);
            return factory.create(type);
        } catch (RuntimeException e) {
            // by default CDI doesn't report exceptions, so we do it here ourselves
            log.error("exception while creating bean for " + getName(), e);
            throw e;
        }
    }
}