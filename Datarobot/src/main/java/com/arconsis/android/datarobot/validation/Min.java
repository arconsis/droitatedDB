package com.arconsis.android.datarobot.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Min validator, that checks the minimum value of a number.
 *
 * @author Falk Appel
 * @author Alexander Frank
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Validator(MinValidator.class)
public @interface Min {
    long value();
}
