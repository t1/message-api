package net.java.messageapi.tck.simple;

import static org.mockito.Mockito.*;

import java.io.File;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.slf4j.*;

@RunWith(Arquillian.class)
public class SimpleIT {
    public static final PomEquippedResolveStage RESOLVER = Maven.resolver().loadPomFromFile("pom.xml");

    /** resolve the files that are required to add that maven artifact */
    public static File[] artifact(String coordinates) {
        return RESOLVER.resolve(coordinates).withTransitivity().asFile();
    }

    private static final Logger log = LoggerFactory.getLogger(SimpleIT.class);

    @Deployment
    public static WebArchive deployment() {
        WebArchive war = ShrinkWrap.create(WebArchive.class, "test.war");
        war.addClasses(ResultWatcher.class, SimpleApi.class, SimpleMDB.class, SimpleSender.class);
        war.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        war.addAsLibraries(artifact("org.mockito:mockito-all"));
        war.addAsLibraries(artifact("net.java.messageapi:adapter"));

        log.debug("deployment:\n{}", war.toString(true));
        return war;
    }

    @Inject
    private SimpleSender sender;

    @Produces
    private static final ResultWatcher result = mock(ResultWatcher.class);

    @Test
    @Ignore
    public void shouldSendSimpleMessage() throws InterruptedException {
        sender.execute();

        Thread.sleep(1000);

        verify(result).invoke();
    }
}
