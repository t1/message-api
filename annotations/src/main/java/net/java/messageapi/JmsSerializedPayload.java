package net.java.messageapi;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.*;

import javax.inject.Qualifier;

/**
 * Convert the payload to/from a {@link java.io.Serializable Serializable} into an {@link javax.jms.ObjectMessage
 * ObjectMessage}.
 */
@Qualifier
@Target({ TYPE, METHOD, FIELD, PARAMETER })
@Retention(RUNTIME)
public @interface JmsSerializedPayload {
    // intentionally empty
}
