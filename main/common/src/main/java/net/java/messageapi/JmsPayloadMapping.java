package net.java.messageapi;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

/**
 * Convert the payload o
 */
@Qualifier
@Target({ FIELD, TYPE })
@Retention(RUNTIME)
public @interface JmsPayloadMapping {
}
