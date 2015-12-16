package com.arconsis.android.datarobot.validation;

/**
 * Implementation of the @Pattern validation annotation
 *
 * @author Falk Appel
 * @author Alexander Frank
 */
public class PatternValidator implements CustomValidator<Pattern, String> {

    public static final int ERROR_CODE = 5;

    @Override
    public ValidationResult onValidate(Pattern annotation, String toByValidated) {
        if (toByValidated == null) {
            return ValidationResult.valid();
        }
        if (java.util.regex.Pattern.compile(annotation.value()).matcher(toByValidated).matches()) {
            return ValidationResult.valid();
        }
        return ValidationResult.invalid(ERROR_CODE, "The given data doesn't match the pattern " + annotation.value());
    }
}
