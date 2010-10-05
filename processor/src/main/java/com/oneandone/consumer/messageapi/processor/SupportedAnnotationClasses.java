package com.oneandone.consumer.messageapi.processor;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.*;

import javax.annotation.processing.SupportedAnnotationTypes;

/**
 * Similar to {@link SupportedAnnotationTypes}, but with class names instead of String constants...
 * which is better for refactoring, etc.
 * 
 * @see AbstractProcessor2
 */
@Documented
@Target(TYPE)
@Retention(RUNTIME)
@Inherited
public @interface SupportedAnnotationClasses {
    Class<? extends Annotation>[] value();
}
