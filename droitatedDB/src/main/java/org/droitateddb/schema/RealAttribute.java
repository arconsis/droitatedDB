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
package org.droitateddb.schema;

import android.database.Cursor;

/**
 * Definition of a REAL Attribute.
 *
 * @author Falk Appel
 * @author Alexander Frank
 */
public class RealAttribute extends AbstractAttribute {

    public RealAttribute(final String fieldName, final Class<?> fieldType, final int columnIdx, ColumnValidator... columnValidators) {
        super(ColumnType.REAL, fieldName, fieldType, columnIdx, columnValidators);
    }

    public RealAttribute(final String fieldName, final String columnName, final Class<?> fieldType, final int columnIdx, ColumnValidator... columnValidators) {
        super(ColumnType.REAL, fieldName, columnName, fieldType, columnIdx, columnValidators);
    }

    @Override
    public Object getNonNullValueFromCursor(final Cursor originalCursor) {
        if (Double.class.isAssignableFrom(type()) || double.class.isAssignableFrom(type())) {
            return originalCursor.getDouble(columnIndex());
        }
        return originalCursor.getFloat(columnIndex());
    }
}
