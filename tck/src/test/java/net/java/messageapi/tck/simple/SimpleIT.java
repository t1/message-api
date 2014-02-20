package net.java.messageapi.tck.simple;

import static org.mockito.Mockito.*;

import java.io.File;
import java.util.*;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.*;

@RunWith(Arquillian.class)
public class SimpleIT {
    private static final int MESSAGE_COUNT = 10;
    private static final int SLEEP = 2000;

    /** resolve the files that are required to add that maven artifact */
    public static File[] artifact(String coordinates) {
        return Maven.resolver().loadPomFromFile("pom.xml").resolve(coordinates).withTransitivity().asFile();
    }

    private static final Logger log = LoggerFactory.getLogger(SimpleIT.class);

    @Deployment
    public static WebArchive deployment() {
        WebArchive war = ShrinkWrap.create(WebArchive.class, "test.war");
        war.addClasses(ResultWatcher.class, SimpleApi.class, SimpleMDB.class, SimpleSender.class);
        war.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        war.addAsLibraries(artifact("org.mockito:mockito-all"));
        war.addAsLibraries(artifact("net.java.messageapi:adapter"));

        log.debug("contents of {}", war.toString(true));
        return war;
    }

    @Inject
    private SimpleSender sender;

    @Produces
    private static final ResultWatcher result = mock(ResultWatcher.class);

    @Test
    public void shouldSendSimpleMessage() throws InterruptedException {
        List<String> uuids = new ArrayList<String>();
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            String uuid = "message-" + i;
            uuids.add(uuid);
            sender.execute(uuid);
            Thread.sleep(500);
        }

        System.out.println("-------------------------------- sleep " + SLEEP);
        Thread.sleep(SLEEP);
        System.out.println("-------------------------------- sleep over");

        for (String uuid : uuids) {
            verify(result).invoke(uuid);
        }
    }
}
