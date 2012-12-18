package net.java.messageapi.test;

import static org.junit.Assert.*;

import java.util.concurrent.Semaphore;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.MessageListener;

import net.java.messageapi.DestinationName;
import net.java.messageapi.MessageApi;
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
public class NestedApiIT {
    @Deployment(name = "test-mdb")
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class, NestedApiIT.class.getName() + ".war") //
        .addClasses(NestedApi.class, NestedApiImpl.class) //
        .addAsLibraries(
                DependencyResolvers.use(MavenDependencyResolver.class) //
                .artifacts("net.java.messageapi:adapter:" + VersionHelper.API_VERSION,
                        "net.java.messageapi:annotations:" + VersionHelper.API_VERSION,
                        "com.google.collections:google-collections:1.0") //
                .resolveAsFiles()) //
        .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml") //
        ;
    }

    @MessageApi
    @DestinationName("queue/test")
    public interface NestedApi {
        public void nestedCall();
    }

    public static String RESULT;
    private static Semaphore semaphore = new Semaphore(0);

    @After
    public void after() {
        RESULT = null;
    }

    @MessageDriven(messageListenerInterface = MessageListener.class, activationConfig = { @ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/test") })
    public static class NestedApiImpl extends MessageDecoder<NestedApi> implements NestedApi {
        @Override
        public void nestedCall() {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            NestedApiIT.RESULT = "nested-test";
            System.out.println("actually called... release semaphore");
            semaphore.release();
        }
    }

    @SuppressWarnings("all")
    @Inject
    NestedApi sender;

    @Test
    public void sendShouldBeAsynchronous() throws Exception {
        assertNull(RESULT);
        System.out.println("calling");
        sender.nestedCall();
        assertNull(RESULT);
        System.out.println("call sent... acquire semaphore");
        semaphore.acquire();
        System.out.println("semaphore acquired... finish test");
        assertEquals("nested-test", RESULT);
    }
}
