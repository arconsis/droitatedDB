package com.arconsis.android.datarobot.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Collects the validation results of multiple validator annotations
 *
 * @author Falk Appel
 * @author Alexander Frank
 */
public class AccumulatedValidationResult {
    private boolean valid;
    private List<ValidationResult> errors = new ArrayList<ValidationResult>();

    public AccumulatedValidationResult() {
        this.valid = true;
    }

    public void addError(ValidationResult error) {
        valid = false;
        errors.add(error);
    }

    public void addErrors(List<ValidationResult> errors) {
        valid = false;
        this.errors.addAll(errors);
    }

    public boolean isValid() {
        return valid;
    }

    public List<ValidationResult> getErrors() {
        return Collections.unmodifiableList(errors);
    }
}
