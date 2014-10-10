package net.java.messageapi;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.*;

import javax.inject.Qualifier;

/**
 * The name of the {@link javax.jms.ConnectionFactory} to be used. This indirectly selects the JMS provider, if there
 * are multiple. If you don't annotate this name, the default is defined in the Java EE 7 spec, chapter EE.5.20 to be
 * <code>java:comp/DefaultJMSConnectionFactory</code>.
 */
@Qualifier
@Target({ FIELD, PARAMETER })
@Retention(RUNTIME)
public @interface ConnectionFactoryName {
    public static final String DEFAULT = "java:comp/DefaultJMSConnectionFactory";

    public String value();
}
