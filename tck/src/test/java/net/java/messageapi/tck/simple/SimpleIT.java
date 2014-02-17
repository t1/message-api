package net.java.messageapi.tck.simple;

import static org.junit.Assert.*;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class SimpleIT {
    @Deployment
    public static WebArchive deployment() {
        WebArchive war = ShrinkWrap.create(WebArchive.class, "test.war");
        war.addClasses(ResultWatcher.class, SimpleApi.class, SimpleMDB.class, SimpleSender.class);
        // beans.xml


        return war;
    }

    @Test
    public void shouldSendSimpleMessage() throws Exception {
        fail("not yet implemented");
    }
}
