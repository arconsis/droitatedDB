package org.droitateddb.validation;

import java.lang.annotation.Annotation;

/**
 * Interface for implementing custom validators, that can be applied to columns in an entity
 *
 * @author Falk Appel
 * @author Alexander Frank
 */
public interface CustomValidator<A extends Annotation, T> {
    ValidationResult onValidate(A annotation, T toByValidated);
}
