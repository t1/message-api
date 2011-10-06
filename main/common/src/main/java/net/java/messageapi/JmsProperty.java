package net.java.messageapi;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

/**
 * Place this parameter in the header, by default in addition to the payload.
 */
@Qualifier
@Target({ PARAMETER })
@Retention(RUNTIME)
public @interface JmsProperty {
    public boolean headerOnly() default false;
}
