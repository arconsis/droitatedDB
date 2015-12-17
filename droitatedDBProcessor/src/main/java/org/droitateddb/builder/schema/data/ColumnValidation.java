package org.droitateddb.builder.schema.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Collect validation information during annotation processing.
 *
 * @author Falk Appel
 * @author Alexander Frank
 */
public class ColumnValidation {
    private List<ValidatorInfo> validators = new ArrayList<ValidatorInfo>();

    public void add(ValidatorInfo validatorInfo) {
        validators.add(validatorInfo);
    }

    public ValidatorInfo get(int idx) {
        return validators.get(idx);
    }

    public int size() {
        return validators.size();
    }
}
