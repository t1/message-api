package net.java.messageapi;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

/**
 * Define the expression to be used to select the messages delivered to this {@link MessageApi} service or
 * {@link MessageEvent} observer.
 * 
 * TODO make this work
 */
@Retention(RUNTIME)
public @interface JmsSelector {
    String value();
}
