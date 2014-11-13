package com.arconsis.android.datarobot.builder.schema.data;

import java.util.Map;

/**
 * Information holder for validation for the creation of the schema.
 *
 * @author Falk Appel
 * @author Alexander Frank
 */
public class ValidatorInfo {
    private String validatorClass;
    private String validatorAnnotation;
    private Map<String, Object> parameter;

    public ValidatorInfo(String validatorClass, String validatorAnnotation, Map<String, Object> parameter) {
        this.validatorClass = validatorClass;
        this.validatorAnnotation = validatorAnnotation;
        this.parameter = parameter;
    }

    public String getValidatorClass() {
        return validatorClass;
    }

    public String getValidatorAnnotation() {
        return validatorAnnotation;
    }

    public Map<String, Object> getParameter() {
        return parameter;
    }

    @Override
    public String toString() {
        return "ValidatorInfo{" +
                "validatorClass='" + validatorClass + '\'' +
                ", validatorAnnotation='" + validatorAnnotation + '\'' +
                ", parameter=" + parameter +
                '}';
    }
}
