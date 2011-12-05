package net.java.messageapi;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.*;

/**
 * Set name of the operation/method or a parameter of a {@link javax.jms.MapMessage}. Default is the method or parameter
 * name itself.
 * 
 * TODO rename to JmsName and add a ParameterNameSupplier for it
 */
@Target({ TYPE, METHOD, FIELD, PARAMETER })
@Retention(RUNTIME)
public @interface JmsMappedName {
    String value();
}
