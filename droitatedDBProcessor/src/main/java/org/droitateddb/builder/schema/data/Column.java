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
package org.droitateddb.builder.schema.data;

import org.droitateddb.schema.ColumnType;

/**
 * @author Falk Appel
 * @author Alexander Frank
 */
public class Column {

    private final String nameInEntity;
    private final String typeInEntity;
    private final ColumnType typeInDb;
    private final boolean isPrimary;
    private final boolean isAutoincrementing;
    private final ColumnValidation columnValidation;

    public Column(final String nameInEntity, final String typeInEntity, final ColumnType typeInDb, final boolean isPrimary, final boolean isAutoincrementing, ColumnValidation columnValidation) {
        this.nameInEntity = nameInEntity;
        this.typeInEntity = typeInEntity;
        this.typeInDb = typeInDb;
        this.isPrimary = isPrimary;
        this.isAutoincrementing = isAutoincrementing;
        this.columnValidation = columnValidation;
    }

    public String getNameInEntity() {
        return nameInEntity;
    }

    public String getTypeInEntity() {
        return typeInEntity;
    }

    public ColumnType getTypeInDb() {
        return typeInDb;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public boolean isAutoincrementing() {
        return isAutoincrementing;
    }

    public ColumnValidation getColumnValidation() {
        return columnValidation;
    }
}
