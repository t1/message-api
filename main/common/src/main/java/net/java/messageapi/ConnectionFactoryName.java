package net.java.messageapi;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

/**
 * The name of the {@link javax.jms.ConnectionFactory} to be used. This indirectly selects the JMS
 * provider, if there are multiple. If you don't annotate this name, the default is
 * "ConnectionFactory".
 */
@Qualifier
@Target(FIELD)
@Retention(RUNTIME)
public @interface ConnectionFactoryName {
    public static final String DEFAULT = "ConnectionFactory";

    public String value();
}
