package net.java.messageapi.test;

import static org.junit.Assert.*;

import java.util.concurrent.Semaphore;

import javax.inject.Inject;

import net.java.messageapi.JmsIncoming;
import net.java.messageapi.MessageApi;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class NoConfigApiIT {
    @Deployment(name = "test-mdb")
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class, "test-mdb.war") //
        // .addClasses(NoConfigApi.class, NoConfigApiMDB.class) //
        .addAsLibraries(
                DependencyResolvers.use(MavenDependencyResolver.class) //
                .artifacts("net.java.messageapi:adapter:2.0-SNAPSHOT", "net.java.messageapi:annotations:2.0-SNAPSHOT",
                        "org.mockito:mockito-all:1.8.5", "com.google.collections:google-collections:1.0") //
                .resolveAsFiles()) //
        .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml") //
        ;
    }

    private static boolean called = false;
    private static Semaphore semaphore = new Semaphore(0);

    @JmsIncoming
    static class NoConfigApiImpl implements NoConfigApi {
        @Override
        public void noConfigCall() {
            called = true;
            semaphore.release();
        }
    }

    @Inject
    NoConfigApi sender;

    @After
    public void after() {
        called = false;
    }

    @Test
    public void sendShouldBeAsynchronous() throws Exception {
        assertFalse(called);
        sender.noConfigCall();
        assertFalse(called);
        semaphore.acquire();
        assertTrue(called);
    }

    @MessageApi
    public interface LocalApi {
        void localApiCall();
    }

    public static class LocalApiImpl implements LocalApi {
        @Override
        public void localApiCall() {
            NoConfigApiIT.ARG0 = "test";
        }
    }

    @Inject
    private LocalApi localApiSender;
    public static String ARG0;

    @Test
    public void simpleCall() {
        localApiSender.localApiCall();
        assertEquals("test", ARG0);
    }
}
