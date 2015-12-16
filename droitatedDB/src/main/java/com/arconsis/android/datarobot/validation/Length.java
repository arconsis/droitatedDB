package com.arconsis.android.datarobot.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Length validator, that checks the length of a String.
 *
 * @author Falk Appel
 * @author Alexander Frank
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Validator(LengthValidator.class)
public @interface Length {
    long min() default -1;

    long max() default -1;
}
