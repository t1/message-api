package net.java.messageapi.adapter;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.*;

public class MessageSenderCdiExtension implements Extension {
    void afterBeanDiscovery(@Observes AfterBeanDiscovery abd, BeanManager bm) {
        System.out.println("beans discovered");
    }
}
