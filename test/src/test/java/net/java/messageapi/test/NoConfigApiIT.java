package net.java.messageapi.test;

import static org.junit.Assert.*;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.MessageListener;

import net.java.messageapi.DestinationName;
import net.java.messageapi.adapter.MessageDecoder;

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
        return ShrinkWrap.create(WebArchive.class, NoConfigApiIT.class.getSimpleName() + ".war") //
        .addClasses(NoConfigApi.class, NoConfigApiImpl.class) //
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

    @MessageDriven(messageListenerInterface = MessageListener.class, activationConfig = { @ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/test") })
    public static class NoConfigApiImpl extends MessageDecoder<NoConfigApi> implements NoConfigApi {
        @Override
        public void noConfigCall() {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            NoConfigApiIT.RESULT = "no-config-test";
            System.out.println(getClass().getSimpleName() + " actually called... release semaphore");
            semaphore.release();
            System.out.println("call done");
        }
    }

    @Inject
    @DestinationName("queue/test")
    NoConfigApi sender;

    @Test
    public void sendShouldBeAsynchronous() throws Exception {
        assertNull(RESULT);
        System.out.println("calling");
        sender.noConfigCall();
        assertNull(RESULT);
        System.out.println("call sent... acquire semaphore");
        boolean acquired = semaphore.tryAcquire(1, TimeUnit.SECONDS);
        assertTrue("couldn't acquire semaphore in time", acquired);
        System.out.println("semaphore acquired... finish test");
        assertEquals("no-config-test", RESULT);
    }
}
