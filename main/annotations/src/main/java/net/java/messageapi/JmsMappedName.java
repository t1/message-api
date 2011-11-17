package net.java.messageapi;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

/**
 * Set name of the operation/method or a parameter of a
 * {@link javax.jms.MapMessage}. Default is the method or parameter name itself.
 * 
 * TODO rename to JmsName and add a ParameterNameSupplier for it
 */
@Qualifier
@Target({ TYPE, METHOD, FIELD, PARAMETER })
@Retention(RUNTIME)
public @interface JmsMappedName {
	String value();
}
