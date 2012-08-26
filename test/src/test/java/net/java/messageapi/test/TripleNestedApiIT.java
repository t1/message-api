package net.java.messageapi.test;

import static org.junit.Assert.*;

import java.util.concurrent.Semaphore;

import javax.inject.Inject;

import net.java.messageapi.*;
import net.java.messageapi.test.TripleNestedApiIT.TripleNestedApiOuterContainer.TripleNestedApiInnerContainer.TripleNestedApi;

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
public class TripleNestedApiIT {
    @Deployment(name = "test-mdb")
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class, TripleNestedApiIT.class.getName() + ".war") //
        .addClasses(TripleNestedApi.class).addClass(TripleNestedApi.class.getName() + "MDB") //
        .addAsLibraries(
                DependencyResolvers.use(MavenDependencyResolver.class) //
                .artifacts("net.java.messageapi:adapter:" + VersionHelper.API_VERSION,
                        "net.java.messageapi:annotations:" + VersionHelper.API_VERSION,
                        "com.google.collections:google-collections:1.0") //
                .resolveAsFiles()) //
        .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml") //
        ;
    }

    public static String RESULT;
    private static Semaphore semaphore = new Semaphore(0);

    @After
    public void after() {
        RESULT = null;
    }

    public static class TripleNestedApiOuterContainer {
        public static class TripleNestedApiInnerContainer {
            @MessageApi
            @DestinationName("queue/test")
            public interface TripleNestedApi {
                void tripleNestedApiCall();
            }
        }
    }

    @JmsIncoming
    public static class TripleNestedApiImpl implements TripleNestedApi {
        @Override
        public void tripleNestedApiCall() {
            TripleNestedApiIT.RESULT = "triple-test";
            System.out.println("actually called... release semaphore");
            semaphore.release();
            System.out.println("call done");
        }
    }

    @SuppressWarnings("all")
    @Inject
    private TripleNestedApi tripleNestedApiSender;

    @Test
    public void shouldCallTripleNestedApi() throws Exception {
        assertNull(RESULT);
        System.out.println("calling");
        tripleNestedApiSender.tripleNestedApiCall();
        assertNull(RESULT);
        System.out.println("call sent... acquire semaphore");
        semaphore.acquire();
        System.out.println("semaphore acquired... finish test");
        assertEquals("triple-test", RESULT);
    }
}
