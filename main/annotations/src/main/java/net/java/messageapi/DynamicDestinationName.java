package net.java.messageapi;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.*;

/**
 * Marks a message-api parameter or message-event field to dynamically provide the name for the JMS destination (queue
 * or topic) that messages should go to/actually come from. Mostly used for dynamic topics.
 * 
 * Note that depending on the JMS provider, all of these destinations have to exist, and each destination has to be
 * bound to one receiver MDB. These can't (yet) be generated dynamically.
 */
@Target({ PARAMETER, FIELD })
@Retention(RUNTIME)
public @interface DynamicDestinationName {
    // no properties
}
