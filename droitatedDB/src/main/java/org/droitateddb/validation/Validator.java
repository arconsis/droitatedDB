package org.droitateddb.validation;

import java.lang.annotation.*;

/**
 * Marks validator annotations.<br>
 * Each annotation that should be used as a validator has to be annotated with this annotation.
 *
 * @author Falk Appel
 * @author Alexander Frank
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Validator {
    Class<? extends CustomValidator<? extends Annotation, ?>> value();
}
