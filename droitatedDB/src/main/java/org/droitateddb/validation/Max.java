package org.droitateddb.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Max validator, that checks the maximum value of a number.
 *
 * @author Falk Appel
 * @author Alexander Frank
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Validator(MaxValidator.class)
public @interface Max {
    long value();
}
