package net.java.messageapi;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

@Qualifier
@Target({ FIELD, PARAMETER, TYPE })
@Retention(RUNTIME)
public @interface DestinationName {
    public String value();
}
