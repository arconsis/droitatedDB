package com.arconsis.android.datarobot.schema;

import com.arconsis.android.datarobot.validation.CustomValidator;

import java.lang.annotation.Annotation;

/**
 * Information holder for validation, containing the validator annotation, the implementation and the parameters.
 *
 * @author Falk Appel
 * @author Alexander Frank
 */
public class ColumnValidator {
    private final Class<? extends Annotation> validatorAnnotation;
    private final Class<? extends CustomValidator<?, ?>> validatorClass;
    private final Object[] params;

    public ColumnValidator(Class<? extends Annotation> validatorAnnotation, Class<? extends CustomValidator<?, ?>> validatorClass, Object... params) {
        this.validatorAnnotation = validatorAnnotation;
        this.validatorClass = validatorClass;
        this.params = params;
    }

    public Class<? extends Annotation> getValidatorAnnotation() {
        return validatorAnnotation;
    }

    public Class<? extends CustomValidator<?, ?>> getValidatorClass() {
        return validatorClass;
    }

    public Object[] getParams() {
        return params;
    }
}
