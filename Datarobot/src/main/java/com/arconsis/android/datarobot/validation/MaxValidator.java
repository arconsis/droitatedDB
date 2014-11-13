package com.arconsis.android.datarobot.validation;

import java.math.BigDecimal;

/**
 * Implementation of the @Max validation annotation
 *
 * @author Falk Appel
 * @author Alexander Frank
 */
public class MaxValidator implements CustomValidator<Max, Number> {

    public static final int ERROR_CODE = 3;

    @Override
    public ValidationResult onValidate(Max annotation, Number toByValidated) {
        long max = annotation.value();

        if (toByValidated == null) {
            return ValidationResult.valid();
        }

        if (new BigDecimal(toByValidated.toString()).compareTo(new BigDecimal(max)) <= 0) {
            return ValidationResult.valid();
        }
        return ValidationResult.invalid(ERROR_CODE, "The field as to be less or equal to " + max);
    }
}
