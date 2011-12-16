package net.java.messageapi;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.*;

import javax.inject.Qualifier;

/**
 * The name of the JMS destination (queue or topic) that messages should go to/come from. Defaults to the fully
 * qualified name of the {@link MessageApi}. Mostly you'd put this annotation on the {@link MessageApi} itself, but you
 * can also annotate the sender and/or the receiver if there is some routing involved that results in different queue
 * names.
 */
@Qualifier
@Target({ TYPE, METHOD, FIELD, PARAMETER })
@Retention(RUNTIME)
public @interface DestinationName {
    public String value();
}
