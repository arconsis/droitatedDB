package com.arconsis.android.datarobot.validation;

/**
 * The result of a validation
 *
 * @author Falk Appel
 * @author Alexander Frank
 */
public class ValidationResult {
    private final boolean valid;
    private final int errorCode;
    private final String errorMessage;

    private ValidationResult(boolean valid, int errorCode, String errorMessage) {
        this.valid = valid;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public boolean isValid() {
        return valid;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public static ValidationResult valid() {
        return new ValidationResult(true, 0, "");
    }

    public static ValidationResult invalid(int errorCode, String error) {
        return new ValidationResult(false, errorCode, error);
    }

    @Override
    public String toString() {
        if (valid) {
            return "valid";
        } else {
            return "(" + errorCode + ") " + errorMessage;
        }
    }
}
