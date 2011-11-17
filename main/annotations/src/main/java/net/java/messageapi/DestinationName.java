package net.java.messageapi;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

/**
 * The name of the JMS destination (queue or topic) that messages should go
 * to/come from. Defaults to the fully qualified name of the {@link MessageApi}.
 * Mostly you'd put this annotation on the {@link MessageApi} itself, but you
 * can also annotate the sender and/or the receiver if there is some routing
 * involved that results in different queue names.
 * 
 * TODO make this really work everywhere
 */
@Qualifier
@Target({ TYPE, METHOD, FIELD, PARAMETER })
@Retention(RUNTIME)
public @interface DestinationName {
	public String value();
}
