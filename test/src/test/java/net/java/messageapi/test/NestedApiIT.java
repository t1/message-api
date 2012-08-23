package net.java.messageapi.test;

import static org.junit.Assert.*;

import java.util.concurrent.Semaphore;

import javax.inject.Inject;

import net.java.messageapi.JmsIncoming;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.junit.*;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
@Ignore
public class NestedApiIT {
    @Deployment(name = "test-mdb")
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class, "test-mdb.war") //
        .addClasses(NestedApi.class, NestedApiMDB.class) //
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

    @After
    public void after() {
        called = false;
    }

    @JmsIncoming
    static class NestedApiImpl implements NestedApi {
        @Override
        public void nestedCall() {
            called = true;
            System.out.println("actually called... release semaphore");
            semaphore.release();
        }
    }

    @Inject
    NestedApi sender;

    @Test
    public void sendShouldBeAsynchronous() throws Exception {
        assertFalse(called);
        System.out.println("calling");
        sender.nestedCall();
        assertFalse(called);
        System.out.println("call sent... wait for semaphore");
        semaphore.acquire();
        System.out.println("semaphore released... test finished");
        assertTrue(called);
    }

    // @Deployment(name = "test-mdb")
    // public static WebArchive createDeployment() {
    // return ShrinkWrap.create(WebArchive.class, "test-mdb.war") //
    // .addClasses(NestedApi.class, NestedApiImpl.class //
    // // , DoubleNestedApi.class, DoubleNestedApiContainer.class, DoubleNestedApiImpl.class
    // ) //
    // .addClass("net.java.messageapi.test.NestedApiIT$NestedApi$NestedApiCall") //
    // .addClass("net.java.messageapi.test.NestedApiIT$NestedApiMDB") //
    // .addAsLibraries(
    // DependencyResolvers.use(MavenDependencyResolver.class) //
    // .artifacts("net.java.messageapi:adapter:2.0-SNAPSHOT", "net.java.messageapi:annotations:2.0-SNAPSHOT",
    // "org.mockito:mockito-all:1.8.5", "com.google.collections:google-collections:1.0") //
    // .resolveAsFiles()) //
    // .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml") //
    // ;
    // }
    //
    // public static String RESULT;
    // private static Semaphore semaphore = new Semaphore(0);
    //
    // @After
    // public void after() {
    // RESULT = null;
    // }
    //
    // @MessageApi
    // @DestinationName("queue/test")
    // public interface NestedApi {
    // void nestedApiCall();
    // }
    //
    // @JmsIncoming
    // public static class NestedApiImpl implements NestedApi {
    // @Override
    // public void nestedApiCall() {
    // NestedApiIT.RESULT = "nested-test";
    // System.out.println("actually called... release semaphore");
    // semaphore.release();
    // }
    // }
    //
    // @Inject
    // private NestedApi nestedApiSender;
    //
    // @Test
    // public void shouldCallNestedApi() throws Exception {
    // System.out.println("calling");
    // nestedApiSender.nestedApiCall();
    // System.out.println("call sent... wait for semaphore");
    // semaphore.acquire();
    // System.out.println("semaphore released... test finished");
    // assertEquals("nested-test", RESULT);
    // }

    // public static class DoubleNestedApiContainer {
    // @MessageApi
    // @DestinationName("queue/test")
    // public interface DoubleNestedApi {
    // void doubleNestedApiCall();
    // }
    // }
    //
    // @JmsIncoming
    // public static class DoubleNestedApiImpl implements DoubleNestedApi {
    // @Override
    // public void doubleNestedApiCall() {
    // NestedApiIT.RESULT = "double-test";
    // semaphore.release();
    // }
    // }
    //
    // @Inject
    // private DoubleNestedApi doubleNestedApiSender;
    //
    // @Test
    // public void shouldCallDoubleNestedApi() throws Exception {
    // doubleNestedApiSender.doubleNestedApiCall();
    // semaphore.acquire();
    // assertEquals("double-test", RESULT);
    // }
}
