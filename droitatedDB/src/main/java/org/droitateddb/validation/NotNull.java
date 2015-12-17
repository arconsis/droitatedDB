package org.droitateddb.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * NotNull validator, that checks if a object is set.
 *
 * @author Falk Appel
 * @author Alexander Frank
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Validator(NotNullValidator.class)
public @interface NotNull {
}
