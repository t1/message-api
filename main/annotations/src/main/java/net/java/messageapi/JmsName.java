package net.java.messageapi;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.*;

/**
 * Set name of the operation/method or a parameter of a {@link javax.jms.MapMessage}. Default is the method or parameter
 * name itself.
 */
@Target({ METHOD, PARAMETER })
@Retention(RUNTIME)
public @interface JmsName {
    String value();
}
