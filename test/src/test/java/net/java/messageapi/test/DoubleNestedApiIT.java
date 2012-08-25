package net.java.messageapi.test;

import static org.junit.Assert.*;

import java.util.concurrent.Semaphore;

import javax.inject.Inject;

import net.java.messageapi.JmsIncoming;
import net.java.messageapi.MessageApi;
import net.java.messageapi.test.DoubleNestedApiIT.DoubleNestedApiContainer.DoubleNestedApi;

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
public class DoubleNestedApiIT {
    @Deployment(name = "test-mdb")
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class, DoubleNestedApiIT.class.getName() + ".war") //
        .addClasses(DoubleNestedApi.class).addClass(DoubleNestedApi.class.getName() + "MDB") //
        .addAsLibraries(
                DependencyResolvers.use(MavenDependencyResolver.class) //
                .artifacts("net.java.messageapi:adapter:2.0-SNAPSHOT", "net.java.messageapi:annotations:2.0-SNAPSHOT",
                        "com.google.collections:google-collections:1.0") //
                .resolveAsFiles()) //
        .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml") //
        ;
    }

    public static String RESULT;
    private static Semaphore in = new Semaphore(0);
    private static Semaphore out = new Semaphore(0);

    protected static void inAcquire() {
        try {
            in.acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @After
    public void after() {
        RESULT = null;
    }

    public static class DoubleNestedApiContainer {
        @MessageApi
        // @DestinationName("queue/test")
        public interface DoubleNestedApi {
            void doubleNestedApiCall();
        }
    }

    @JmsIncoming
    public static class DoubleNestedApiImpl implements DoubleNestedApi {
        @Override
        public void doubleNestedApiCall() {
            System.out.println("actually called... acquire in-semaphore");
            inAcquire();
            DoubleNestedApiIT.RESULT = "double-test";
            System.out.println("release out-semaphore");
            out.release();
            System.out.println("call done");
        }
    }

    @SuppressWarnings("all")
    @Inject
    private DoubleNestedApi doubleNestedApiSender;

    @Test
    public void shouldCallDoubleNestedApi() throws Exception {
        assertNull(RESULT);
        System.out.println("calling");
        doubleNestedApiSender.doubleNestedApiCall();
        assertNull(RESULT);
        System.out.println("call sent... release in-semaphore");
        in.release();
        System.out.println("... acquire out-semaphore");
        out.acquire();
        System.out.println("out-semaphore acquired... finish test");
        assertEquals("double-test", RESULT);
    }
}
