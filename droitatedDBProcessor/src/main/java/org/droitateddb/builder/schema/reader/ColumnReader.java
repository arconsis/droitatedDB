/*
 * Copyright (C) 2014 The droitated DB Authors
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
package org.droitateddb.builder.schema.reader;

import org.droitateddb.builder.schema.data.Column;
import org.droitateddb.builder.schema.data.ColumnValidation;
import org.droitateddb.entity.AutoIncrement;
import org.droitateddb.entity.PrimaryKey;
import org.droitateddb.schema.ColumnType;

import javax.annotation.processing.Messager;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic.Kind;

/**
 * @author Falk Appel
 * @author Alexander Frank
 */
public class ColumnReader implements Reader<Column> {

    private final VariableElement column;
    private final Elements elements;
    private final Messager messager;

    public ColumnReader(final VariableElement column, final Elements elements, final Messager messager) {
        this.column = column;
        this.elements = elements;
        this.messager = messager;
    }

    @Override
    public Column read() {
        String nameInEntity = column.getSimpleName().toString();
        String typeInEntity = column.asType().toString();

        try {
            boolean isPrimaryKey = isPrimaryKeyAnnotated(column);
            boolean isAutoincrementing = isAutoIncrementAnnotated(column);

            checkPrimaryKeyAndAutoincrementLocation(isPrimaryKey, isAutoincrementing, column, typeInEntity);

            ColumnValidation columnValidation = new ColumnValidationReader(column, elements, messager).read();

            return new Column(nameInEntity, typeInEntity, ColumnType.resolveColumnType(typeInEntity), isPrimaryKey, isAutoincrementing, columnValidation);
        } catch (IllegalStateException e) {
            messager.printMessage(Kind.ERROR, e.getMessage(), column);
            return null;
        }
    }

    private boolean isPrimaryKeyAnnotated(final VariableElement variableElement) {
        return variableElement.getAnnotation(PrimaryKey.class) != null;
    }

    private boolean isAutoIncrementAnnotated(final VariableElement variableElement) {
        return variableElement.getAnnotation(AutoIncrement.class) != null;
    }

    private void checkPrimaryKeyAndAutoincrementLocation(final boolean hasPrimaryKey, final boolean hasAutoIncrement, final VariableElement variableElement,
                                                         final String fieldType) {
        if (!hasPrimaryKey && hasAutoIncrement) {
            messager.printMessage(Kind.ERROR, "@AutoIncrement is only allowed for @PrimaryKey fields", variableElement);
        } else if (hasPrimaryKey && fieldIsNotAWholeNumber(fieldType)) {
            messager.printMessage(Kind.ERROR, "@PrimaryKey is only allowed for non-primitiv whole-numbers", variableElement);
        }
    }

    private boolean fieldIsNotAWholeNumber(final String fieldType) {
        return !Integer.class.getCanonicalName().equals(fieldType) && //
                !Long.class.getCanonicalName().equals(fieldType);
    }
}
