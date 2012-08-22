package net.java.messageapi.test;

import static org.junit.Assert.*;

import javax.inject.Inject;

import net.java.messageapi.DestinationName;
import net.java.messageapi.MessageApi;
import net.java.messageapi.test.NestedApiIT.DoubleNestedApiContainer.DoubleNestedApi;

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
    public static String RESULT;

    @After
    public void after() {
        RESULT = null;
    }

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

    @MessageApi
    @DestinationName("queue/test")
    public interface NestedApi {
        void nestedApiCall();
    }

    public static class NestedApiImpl implements NestedApi {
        @Override
        public void nestedApiCall() {
            NestedApiIT.RESULT = "nested-test";
        }
    }

    @Inject
    private NestedApi nestedApiSender;

    @Test
    public void shouldCallNestedApi() {
        nestedApiSender.nestedApiCall();
        assertEquals("nested-test", RESULT);
    }

    public static class DoubleNestedApiContainer {
        @MessageApi
        @DestinationName("queue/test")
        public interface DoubleNestedApi {
            void doubleNestedApiCall();
        }
    }

    public static class DoubleNestedApiImpl implements DoubleNestedApi {
        @Override
        public void doubleNestedApiCall() {
            NestedApiIT.RESULT = "double-test";
        }
    }

    @Inject
    private DoubleNestedApi doubleNestedApiSender;

    @Test
    public void shouldCallDoubleNestedApi() {
        doubleNestedApiSender.doubleNestedApiCall();
        assertEquals("double-test", RESULT);
    }
}
