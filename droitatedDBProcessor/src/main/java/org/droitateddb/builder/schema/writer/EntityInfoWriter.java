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
package org.droitateddb.builder.schema.writer;

import org.droitateddb.builder.schema.data.Column;
import org.droitateddb.builder.schema.data.Table;
import org.droitateddb.schema.SchemaConstants;

import java.util.Set;

import static org.droitateddb.builder.Constants.CONSTANT_PREFIX;

/**
 * @author Alexander Frank
 * @author Falk Appel
 */
public class EntityInfoWriter implements Writer {

    private final String indent;
    private final Table table;

    public EntityInfoWriter(final String indent, final Table table) {
        this.indent = indent;
        this.table = table;
    }

    @Override
    public String write() {
        StringBuilder builder = new StringBuilder();
        builder.append(indent).append(CONSTANT_PREFIX).append("EntityInfo ").append(table.getName()).append(SchemaConstants.INFO_SUFFIX) //
                .append(" = new EntityInfo(\"").append(table.getEntityClassName()).append("\", ") //
                .append("\"").append(table.getName()).append("\", ") //
                .append(table.getName()).append(SchemaConstants.TABLE).append(".class, ")
                .append(hasValidation()).append(");")
                .append("\n");
        return builder.toString();
    }

    private boolean hasValidation() {
        Set<Column> columns = table.getColumns();
        for (Column column : columns) {
            if (column.getColumnValidation().size() > 0) {
                return true;
            }
        }
        return false;
    }

}
