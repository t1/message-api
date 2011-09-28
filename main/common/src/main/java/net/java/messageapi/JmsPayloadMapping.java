package net.java.messageapi;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

/**
 * Convert the payload to/from a {@link javax.jms.MapMessage}.
 */
@Qualifier
@Target({ FIELD, TYPE })
@Retention(RUNTIME)
public @interface JmsPayloadMapping {
    /** The message field that contians the name of the operation/method to be performed */
    String operationName() default "OPERATION";

    /** Make the names of the fields uppercase */
    boolean upperCaseFields() default false;
}
