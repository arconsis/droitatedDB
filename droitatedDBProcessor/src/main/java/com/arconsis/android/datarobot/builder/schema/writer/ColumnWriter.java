/*
 * Copyright (C) 2014 The Datarobot Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.arconsis.android.datarobot.builder.schema.writer;

import com.arconsis.android.datarobot.builder.schema.data.Column;
import com.arconsis.android.datarobot.builder.schema.data.ColumnValidation;
import com.arconsis.android.datarobot.builder.schema.data.ValidatorInfo;
import com.arconsis.android.datarobot.schema.ColumnValidator;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static com.arconsis.android.datarobot.builder.Constants.CONSTANT_PREFIX;
import static com.arconsis.android.datarobot.builder.Constants.TAB;
import static com.arconsis.android.datarobot.schema.SchemaConstants.ATTRIBUTE_SUFFIX;

/**
 * @author Alexander Frank
 * @author Falk Appel
 */
public class ColumnWriter implements Writer {

    private final String indent;
    private final Column column;
    private final int columnIdx;

    public ColumnWriter(final String indent, final Column column, final int columnIdx) {
        this.indent = indent;
        this.column = column;
        this.columnIdx = columnIdx;
    }

    @Override
    public String write() {
        String columnType = column.getTypeInDb().getReadable();
        StringBuilder builder = new StringBuilder();
        String nameToUpper = column.getNameInEntity().toUpperCase(Locale.getDefault());

        builder.append(indent)
                .append(TAB)
                .append(CONSTANT_PREFIX)
                .append(columnType)
                .append(ATTRIBUTE_SUFFIX)
                .append(" ")
                .append(nameToUpper)
                .append(" = new ")
                .append(columnType)
                .append(ATTRIBUTE_SUFFIX)
                .append("(\"")
                .append(column.getNameInEntity())
                .append("\", ")
                .append(column.getTypeInEntity())
                .append(".class, ")
                .append(columnIdx);

        appendColumnValidators(builder);
        builder.append(");\n");
        return builder.toString();
    }

    private void appendColumnValidators(StringBuilder builder) {
        ColumnValidation columnValidation = column.getColumnValidation();
        int amountOfValidator = columnValidation.size();
        if (amountOfValidator > 0) {
            builder.append(", ");
            for (int i = 0; i < amountOfValidator; i++) {
                addColumnValidator(builder, columnValidation.get(i));
                if (i < amountOfValidator - 1) {
                    builder.append(", ");
                }
            }
        }
    }

    private void addColumnValidator(StringBuilder builder, ValidatorInfo validatorInfo) {
        builder.append("new ")
                .append(ColumnValidator.class.getSimpleName())
                .append("(")
                .append(validatorInfo.getValidatorAnnotation()).append(".class")
                .append(", ")
                .append(validatorInfo.getValidatorClass()).append(".class");

        int count = 0;
        Set<Map.Entry<String, Object>> entries = validatorInfo.getParameter().entrySet();
        int amountOfParams = entries.size();

        if (amountOfParams > 0) {
            builder.append(", ");
            for (Map.Entry<String, Object> param : entries) {
                builder.append("\"").append(param.getKey()).append("\"").append(", ");
                Object value = param.getValue();
                if (value.getClass().equals(String.class)) {
                    builder.append("\"").append(value).append("\"");
                } else {
                    builder.append(value);
                }
                if (count++ < amountOfParams - 1) {
                    builder.append(", ");
                }
            }
        }

        builder.append(")");
    }

    public String getSql() {
        StringBuilder builder = new StringBuilder(column.getNameInEntity());
        builder.append(" ").append(column.getTypeInDb().getReadable());
        if (column.isPrimary()) {
            builder.append(" PRIMARY KEY");
        }
        if (column.isAutoincrementing()) {
            builder.append(" AUTOINCREMENT");
        }
        return builder.toString();
    }
}
