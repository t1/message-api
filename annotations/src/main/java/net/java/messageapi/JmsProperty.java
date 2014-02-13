package net.java.messageapi;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Place this parameter of a MessageApi method into the JMS message header instead of the payload.
 * <p>
 * If you annotate some indirect field of such a parameter, it will be in the header, too, but it will still be part of
 * the payload. To remove it from the payload, make that field transient or annotate it as
 * {@link javax.xml.bind.annotation.XmlTransient XmlTransient}.
 */
@Target({ PARAMETER, FIELD })
@Retention(RUNTIME)
public @interface JmsProperty {
    // intentionally empty
}
