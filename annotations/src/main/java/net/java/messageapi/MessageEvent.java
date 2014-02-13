package net.java.messageapi;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.*;

/**
 * TODO document
 * 
 * @author Rüdiger zu Dohna
 */
@Documented
@Target(TYPE)
@Retention(RUNTIME)
public @interface MessageEvent {
    /**
     * If you want, you can let the annotation processor generate an MDB for this MessageEvent. This not useful for
     * MessageApis, as then the receiver will be the MDB itself, but it is handy for MessageEvents, as then the MDB is
     * just the bridge between JMS and CDI events.
     */
    public boolean generateMdb() default false;
}
