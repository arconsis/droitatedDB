package org.droitateddb.validation;

import java.util.List;

/**
 * This exception is thrown when you try to save invalid entity
 *
 * @author Falk Appel
 * @author Alexander Frank
 */
public class InvalidEntityException extends RuntimeException {

    private List<ValidationResult> errors;

    public InvalidEntityException(AccumulatedValidationResult errors) {
        super(flatten(errors));
        this.errors = errors.getErrors();

    }

    private static String flatten(AccumulatedValidationResult errors) {
        if (errors == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        List<ValidationResult> validationResults = errors.getErrors();

        for (int i = 0; i < validationResults.size(); i++) {
            builder.append(validationResults.get(i));

            if (i < validationResults.size() - 1) {
                builder.append(" | ");
            }
        }
        return builder.toString();
    }

    public List<ValidationResult> getErrors() {
        return errors;
    }
}
