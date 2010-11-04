package net.java.messageapi.adapter;

import java.util.Map;

import com.google.common.collect.Maps;

/**
 * @see MessageSenderFactory
 */
public class MessageSenderRegistry {

    private final Map<Class<?>, MessageSenderFactory<?>> map = Maps.newHashMap();

    public <T> void add(Class<T> api, MessageSenderFactory<T> adapter) {
        if (map.containsKey(api))
            throw new IllegalArgumentException("already registered " + api);
        map.put(api, adapter);
    }

    public <T> void remove(Class<T> api) {
        if (map.remove(api) == null) {
            throw new IllegalArgumentException("not registered " + api);
        }
    }

    public <T> T get(Class<T> api) {
        MessageSenderFactory<?> factory = map.get(api);
        if (factory == null)
            throw new IllegalArgumentException("unknown api " + api);
        return api.cast(factory.get());
    }
}
