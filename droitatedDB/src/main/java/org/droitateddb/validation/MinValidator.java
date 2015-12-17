package org.droitateddb.validation;

import java.math.BigDecimal;

/**
 * Implementation of the @Min validation annotation
 *
 * @author Falk Appel
 * @author Alexander Frank
 */
public class MinValidator implements CustomValidator<Min, Number> {

    public static final int ERROR_CODE = 2;

    @Override
    public ValidationResult onValidate(Min annotation, Number toByValidated) {
        long min = annotation.value();

        if (toByValidated == null) {
            return ValidationResult.valid();
        }

        if (new BigDecimal(toByValidated.toString()).compareTo(new BigDecimal(min)) >= 0) {
            return ValidationResult.valid();
        }
        return ValidationResult.invalid(ERROR_CODE, "The field as to be greater or equal to " + min);
    }
}
