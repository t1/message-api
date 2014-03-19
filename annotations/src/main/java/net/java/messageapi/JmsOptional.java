package net.java.messageapi;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.*;

/**
 * Mark this Parameter of the {@link MessageApi} as optional, i.e. it can be <code>null</code>.
 */
@Documented
@Target(PARAMETER)
@Retention(RUNTIME)
public @interface JmsOptional {
    // empty
}
