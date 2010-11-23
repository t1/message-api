package net.java.messageapi.test;

import java.util.*;

import net.java.messageapi.adapter.*;

import org.junit.Test;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

public class MessageSenderRegistryTest {

    public interface TestApi1 {
        public void call();
    }

    public interface TestApi2 {
        public void call2();
    }

    private static final Supplier<Properties> EMPTY_PROPERTIES = Suppliers.ofInstance(new Properties());

    private static final Supplier<Map<String, Object>> EMPTY_MAP = Suppliers.ofInstance(Collections.<String, Object> emptyMap());

    private final MessageSenderRegistry registry = new MessageSenderRegistry();

    private final JmsConfig config1 = new XmlJmsConfig("aaa", "bbb", "ccc", "ddd", true,
            EMPTY_PROPERTIES, EMPTY_MAP);
    private final JmsConfig config2 = new XmlJmsConfig("eee", "fff", "ggg", "hhh", true,
            EMPTY_PROPERTIES, EMPTY_MAP);

    @Test
    public void canAddOnce() throws Exception {
        registry.add(TestApi1.class, config1.createFactory(TestApi1.class));
    }

    @Test
    public void canAddTwoDifferentApis() throws Exception {
        registry.add(TestApi1.class, config1.createFactory(TestApi1.class));
        registry.add(TestApi2.class, config2.createFactory(TestApi2.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void cantAddSameApiTwice() throws Exception {
        registry.add(TestApi1.class, config1.createFactory(TestApi1.class));
        registry.add(TestApi1.class, config2.createFactory(TestApi1.class));
    }

    @Test
    public void canRemoveApi() throws Exception {
        registry.add(TestApi1.class, config1.createFactory(TestApi1.class));
        registry.remove(TestApi1.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cantRemoveEmpty() throws Exception {
        registry.remove(TestApi1.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cantRemoveWrongApi() throws Exception {
        registry.add(TestApi1.class, config1.createFactory(TestApi1.class));
        registry.remove(TestApi2.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cantRemoveTwice() throws Exception {
        registry.add(TestApi1.class, config1.createFactory(TestApi1.class));
        registry.remove(TestApi1.class);
        registry.remove(TestApi1.class);
    }

    @Test
    public void canGetApi() throws Exception {
        registry.add(TestApi1.class, config1.createFactory(TestApi1.class));
        registry.get(TestApi1.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cantGetEmptyApi() throws Exception {
        registry.get(TestApi2.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cantGetWrongApi() throws Exception {
        registry.add(TestApi1.class, config1.createFactory(TestApi1.class));
        registry.get(TestApi2.class);
    }
}
