package net.java.messageapi.adapter;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Similar to {@link javax.xml.bind.annotation.XmlType#propOrder() XmlType#propOrder}, but it includes
 * {@link javax.xml.bind.annotation.XmlTransient transient} properties, so {@link net.java.messageapi.JmsProperty
 * JmsProperty}-annotated properties can be resolved, too.
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface PropOrder {
    String[] value();
}
