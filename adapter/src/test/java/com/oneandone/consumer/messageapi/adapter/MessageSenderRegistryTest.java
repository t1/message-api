package com.oneandone.consumer.messageapi.adapter;

import org.junit.Ignore;
import org.junit.Test;

import com.oneandone.consumer.messageapi.adapter.xml.JmsXmlSenderFactory;

@Ignore
public class MessageSenderRegistryTest {

    public interface TestApi1 {
        public void call();
    }

    public interface TestApi2 {
        public void call2();
    }

    private final MessageSenderRegistry registry = new MessageSenderRegistry();

    private final JmsConfig config1 = DefaultJmsConfigFactory.getJmsConfig("aaa", "bbb", "ccc",
            "ddd");
    private final JmsConfig config2 = DefaultJmsConfigFactory.getJmsConfig("eee", "fff", "ggg",
            "hhh");

    @Test
    public void canAddOnce() throws Exception {
        registry.add(TestApi1.class, JmsXmlSenderFactory.create(TestApi1.class, config1));
    }

    @Test
    public void canAddTwoDifferentApis() throws Exception {
        registry.add(TestApi1.class, JmsXmlSenderFactory.create(TestApi1.class, config1));
        registry.add(TestApi2.class, JmsXmlSenderFactory.create(TestApi2.class, config2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void cantAddSameApiTwice() throws Exception {
        registry.add(TestApi1.class, JmsXmlSenderFactory.create(TestApi1.class, config1));
        registry.add(TestApi1.class, JmsXmlSenderFactory.create(TestApi1.class, config2));
    }

    @Test
    public void canRemoveApi() throws Exception {
        registry.add(TestApi1.class, JmsXmlSenderFactory.create(TestApi1.class, config1));
        registry.remove(TestApi1.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cantRemoveEmpty() throws Exception {
        registry.remove(TestApi1.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cantRemoveWrongApi() throws Exception {
        registry.add(TestApi1.class, JmsXmlSenderFactory.create(TestApi1.class, config1));
        registry.remove(TestApi2.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cantRemoveTwice() throws Exception {
        registry.add(TestApi1.class, JmsXmlSenderFactory.create(TestApi1.class, config1));
        registry.remove(TestApi1.class);
        registry.remove(TestApi1.class);
    }

    @Test
    public void canGetApi() throws Exception {
        registry.add(TestApi1.class, JmsXmlSenderFactory.create(TestApi1.class, config1));
        registry.get(TestApi1.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cantGetEmptyApi() throws Exception {
        registry.get(TestApi2.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cantGetWrongApi() throws Exception {
        registry.add(TestApi1.class, JmsXmlSenderFactory.create(TestApi1.class, config1));
        registry.get(TestApi2.class);
    }
}
