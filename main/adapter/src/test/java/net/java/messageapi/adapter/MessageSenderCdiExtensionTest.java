package net.java.messageapi.adapter;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

import java.lang.annotation.Annotation;
import java.util.*;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.*;
import javax.inject.Singleton;

import net.java.messageapi.MessageApi;
import net.sf.twip.TwiP;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;

import com.google.common.collect.ImmutableSet;

@RunWith(TwiP.class)
@Ignore
public class MessageSenderCdiExtensionTest {

    @MessageApi
    public interface AnnotatedInterface {
    }

    private final MessageSenderCdiExtension extension = new MessageSenderCdiExtension();

    @Mock
    private AfterBeanDiscovery event;

    @Mock
    private BeanManager manager;

    @Mock
    private Bean<Object> bean;

    @Mock
    private InjectionPoint injectionPoint;

    @Captor
    ArgumentCaptor<Bean<?>> captor;

    @Test
    public void testname() throws Exception {
        // given
        Set<Bean<?>> beans = ImmutableSet.<Bean<?>> of(bean);
        given(manager.getBeans(Object.class)).willReturn(beans);

        Set<InjectionPoint> injectionPoints = ImmutableSet.of(injectionPoint);
        given(bean.getInjectionPoints()).willReturn(injectionPoints);

        given(injectionPoint.getType()).willReturn(AnnotatedInterface.class);

        // when
        // extension.afterBeanDiscovery(event, manager);

        // then
        verify(event).addBean(captor.capture());
        Bean<?> addedBean = captor.getValue();
        assertEquals(AnnotatedInterface.class, addedBean.getBeanClass());
        assertEquals(Collections.emptySet(), addedBean.getInjectionPoints());
        assertNull(addedBean.getName());

        Iterator<Annotation> qualifiers = addedBean.getQualifiers().iterator();
        assertEquals(Default.class, qualifiers.next().annotationType());
        assertEquals(Any.class, qualifiers.next().annotationType());
        assertFalse(qualifiers.hasNext());

        assertEquals(Singleton.class, addedBean.getScope());
        assertTrue(addedBean.getStereotypes().isEmpty());
        assertEquals(ImmutableSet.of(AnnotatedInterface.class, Object.class), addedBean.getTypes());
        assertFalse(addedBean.isAlternative());
        assertFalse(addedBean.isNullable());
    }
}
