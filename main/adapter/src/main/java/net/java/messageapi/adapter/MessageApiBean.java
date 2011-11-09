package net.java.messageapi.adapter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;

import net.java.messageapi.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

final class MessageApiBean<T> implements Bean<T> {
    private final Logger log = LoggerFactory.getLogger(MessageApiBean.class);

    public static <T> MessageApiBean<T> of(Class<T> api, Annotation... qualifiers) {
        return MessageApiBean.of(api, ImmutableSet.<Annotation> of(qualifiers));
    }

    public static <T> MessageApiBean<T> of(Class<T> api, Set<Annotation> qualifiers) {
        return new MessageApiBean<T>(api, qualifiers);
    }

    private final Class<T> api;
    private final Set<Annotation> qualifiers;
    final JmsSenderFactory factory;

    private MessageApiBean(Class<T> api, Set<Annotation> qualifiers) {
        this.api = api;
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
            return api.getCanonicalName();
        return destinationName.value();
    }

    private <Q> Q getQualifier(Class<Q> type) {
        for (Annotation qualifier : qualifiers) {
            if (type.isInstance(qualifier)) {
                return type.cast(qualifier);
            }
        }
        return null;
    }

    private JmsPayloadHandler getPayloadHandler() {
        JmsPayloadMapping mapping = api.getAnnotation(JmsPayloadMapping.class);
        if (mapping == null)
            return new XmlJmsPayloadHandler();
        return new MapJmsPayloadHandler(new MappingBuilder(api).build());
    }

    @Override
    public Class<?> getBeanClass() {
        return api;
    }

    @Override
    public Set<InjectionPoint> getInjectionPoints() {
        return Collections.emptySet();
    }

    @Override
    public String getName() {
        return api.getName() + qualifiers;
    }

    @Override
    public Set<Annotation> getQualifiers() {
        return qualifiers;
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return Dependent.class;
    }

    @Override
    public Set<Class<? extends Annotation>> getStereotypes() {
        return Collections.emptySet();
    }

    @Override
    public Set<Type> getTypes() {
        return Sets.<Type> newHashSet(api, Object.class);
    }

    @Override
    public boolean isAlternative() {
        return false;
    }

    @Override
    public boolean isNullable() {
        return false;
    }

    @Override
    public T create(CreationalContext<T> ctx) {
        try {
            log.info("create message api bean {} qualified as {}", api.getSimpleName(), qualifiers);
            return factory.create(api);
        } catch (RuntimeException e) {
            // by default CDI doesn't report exceptions, so we do it here ourselves
            log.error("exception while creating bean for " + getName(), e);
            throw e;
        }
    }

    @Override
    public void destroy(T instance, CreationalContext<T> ctx) {
        ctx.release();
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ": " + getName() + " with " + getQualifiers();
    }
}