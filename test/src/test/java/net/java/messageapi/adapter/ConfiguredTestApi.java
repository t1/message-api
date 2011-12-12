package net.java.messageapi.adapter;

import net.java.messageapi.*;

@MessageApi
@JmsMappedPayload
public interface ConfiguredTestApi {
    // must be arg0, so it runs without the api annotation processor as well as with it
    public void configuredMethod(@JmsMappedName("aarg") String arg0);
}