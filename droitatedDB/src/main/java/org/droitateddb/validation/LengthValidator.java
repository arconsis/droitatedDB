package org.droitateddb.validation;

/**
 * Implementation of the @Length validation annotation
 *
 * @author Falk Appel
 * @author Alexander Frank
 */
public class LengthValidator implements CustomValidator<Length, String> {

    public static final int ERROR_CODE = 1;

    @Override
    public ValidationResult onValidate(Length length, String data) {
        long minLength = length.min();
        long maxLength = length.max();
        if (data == null) {
            return ValidationResult.valid();
        }
        if (minLength > -1 && data.length() < minLength) {
            return ValidationResult.invalid(ERROR_CODE, "The given data is to short. It should be between " + minLength + " and " + maxLength + " characters long.");
        }
        if (maxLength > -1 && data.length() > maxLength) {
            return ValidationResult.invalid(ERROR_CODE, "The given data is to long. It should be between " + minLength + " and " + maxLength + " characters long.");
        }
        return ValidationResult.valid();
    }
}
