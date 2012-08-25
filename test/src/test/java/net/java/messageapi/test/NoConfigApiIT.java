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
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class NoConfigApiIT {
    @Deployment(name = "test-mdb")
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class, NoConfigApiIT.class.getName() + ".war") //
        .addClasses(NoConfigApi.class, NoConfigApiMDB.class) //
        .addAsLibraries(
                DependencyResolvers.use(MavenDependencyResolver.class) //
                .artifacts("net.java.messageapi:adapter:2.0-SNAPSHOT", "net.java.messageapi:annotations:2.0-SNAPSHOT",
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

    @JmsIncoming
    static class NoConfigApiImpl implements NoConfigApi {
        @Override
        public void noConfigCall() {
            NoConfigApiIT.RESULT = "no-config-test";
            System.out.println("actually called... release semaphore");
            semaphore.release();
            System.out.println("call done");
        }
    }

    @SuppressWarnings("all")
    @Inject
    NoConfigApi sender;

    @Test
    public void sendShouldBeAsynchronous() throws Exception {
        assertNull(RESULT);
        System.out.println("calling");
        sender.noConfigCall();
        assertNull(RESULT);
        System.out.println("call sent... acquire semaphore");
        semaphore.acquire();
        System.out.println("semaphore acquired... finish test");
        assertEquals("no-config-test", RESULT);
    }
}
