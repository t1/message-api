package net.java.messageapi.test;

import java.io.StringWriter;

import javax.xml.bind.JAXB;

import net.java.messageapi.adapter.AbstractJmsSenderFactory;
import net.java.messageapi.adapter.JmsConfig;
import net.sf.twip.TwiP;
import net.sf.twip.Values;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(TwiP.class)
public class LoadJmsSenderFactoryTest {

    public static final Class<?>[] API = { TestApi.class, MappedApi.class };

    @Test
    public void messageRoundtrip(@Values("API") Class<?> api) throws Exception {
        JmsConfig config = JmsConfig.getConfigFor(api);
        AbstractJmsSenderFactory<TestApi, ?> factory = config.createFactory(TestApi.class);

        StringWriter writer = new StringWriter();
        JAXB.marshal(factory, writer);

        // FIXME
        System.out.println(writer);
    }
}
