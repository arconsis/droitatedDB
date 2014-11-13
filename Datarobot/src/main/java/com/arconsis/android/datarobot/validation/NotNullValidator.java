package com.arconsis.android.datarobot.validation;

/**
 * Implementation of the @NotNull validation annotation.
 *
 * @author Falk Appel
 * @author Alexander Frank
 */
public class NotNullValidator implements CustomValidator<NotNull, Object> {

    public static final int ERROR_CODE = 0;

    @Override
    public ValidationResult onValidate(NotNull annotation, Object toByValidated) {
        if (toByValidated == null) {
            return ValidationResult.invalid(ERROR_CODE, "The field is not allowed to be null");
        }
        return ValidationResult.valid();
    }
}
