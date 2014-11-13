package com.arconsis.android.datarobot.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Pattern validator, that matches a String to the given regular expression
 *
 * @author Falk Appel
 * @author Alexander Frank
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Validator(PatternValidator.class)
public @interface Pattern {
	String value();
}
