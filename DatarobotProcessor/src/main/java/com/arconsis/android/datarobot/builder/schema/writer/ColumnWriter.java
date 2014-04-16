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

import static com.arconsis.android.datarobot.builder.Constants.CONSTANT_PREFIX;
import static com.arconsis.android.datarobot.builder.Constants.TAB;
import static com.arconsis.android.datarobot.schema.SchemaConstants.ATRIBUTE_SUFFIX;

import java.util.Locale;

import com.arconsis.android.datarobot.builder.schema.data.Column;

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

		builder.append(indent).append(TAB).append(CONSTANT_PREFIX).append(columnType).append(ATRIBUTE_SUFFIX).append(" ").append(nameToUpper).append(" = new ")
		.append(columnType).append(ATRIBUTE_SUFFIX).append("(\"").append(column.getNameInEntity()).append("\", ").append(column.getTypeInEntity())
		.append(".class, ").append(columnIdx).append(");\n");
		return builder.toString();
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
