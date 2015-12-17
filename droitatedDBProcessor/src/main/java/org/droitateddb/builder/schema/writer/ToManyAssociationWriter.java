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
package org.droitateddb.builder.schema.writer;

import org.droitateddb.schema.SchemaConstants;
import org.droitateddb.util.Pair;

import java.util.Locale;

import static org.droitateddb.builder.Constants.CONSTANT_ATTRIBUTE_ARRAY;
import static org.droitateddb.builder.Constants.CONSTANT_PREFIX;
import static org.droitateddb.builder.Constants.CONSTANT_STRING;
import static org.droitateddb.builder.Constants.CONSTANT_STRING_ARRAY;
import static org.droitateddb.builder.Constants.TAB;
import static java.lang.String.format;

/**
 * @author Alexander Frank
 * @author Falk Appel
 */
public class ToManyAssociationWriter implements Writer {

	private final String indent;
	private final String tableName;
	private final String simpleLeft;
	private final String simpleLeftLower;
	private final String canonicalLeft;
	private final String simpleRight;
	private final String simpleRightLower;
	private final String canonicalRight;

	public ToManyAssociationWriter(final String indent, final Pair<String, String> link) {
		this.indent = indent;

		canonicalLeft = link.getFirst();
		simpleLeft = getSimpleName(canonicalLeft);
		simpleLeftLower = simpleLeft.toLowerCase(Locale.getDefault());
		canonicalRight = link.getSecond();
		simpleRight = getSimpleName(canonicalRight);
		simpleRightLower = simpleRight.toLowerCase(Locale.getDefault());

		tableName = simpleLeft + simpleRight + SchemaConstants.LINK;
	}

	@Override
	public String write() {
		StringBuilder builder = new StringBuilder();
		addDefinition(builder);
		addTableName(builder);
		addAttribute(builder, canonicalLeft, simpleLeftLower, 0, SchemaConstants.FROM_SUFFIX);
		addAttribute(builder, canonicalRight, simpleRightLower, 1, SchemaConstants.TO_SUFFIX);
		builder.append("\n");
		addSqlStatement(builder);
		addProjection(builder);
		addAttributes(builder);
		addEnd(builder);
		return builder.toString();
	}

	private void addDefinition(final StringBuilder builder) {
		builder.append(indent).append("public interface ").append(simpleLeft).append(simpleRight).append(SchemaConstants.LINK).append(" {\n");
	}

	private void addTableName(final StringBuilder builder) {
		builder.append(indent).append(TAB).append(String.format(CONSTANT_STRING, SchemaConstants.TABLE_NAME, tableName));
		builder.append("\n");
	}

	private void addAttribute(final StringBuilder builder, final String canonicalName, final String simpleName, final int idx, final String suffix) {
		String fkToUpper = (SchemaConstants.FOREIGN_KEY + simpleName + suffix).toUpperCase(Locale.getDefault());

		builder.append(indent).append(TAB).append(CONSTANT_PREFIX).append("IntegerAttribute ").append(fkToUpper).append(" = ") //
				.append("new IntegerAttribute(") //
				.append("\"").append("\", \"") //
				.append(SchemaConstants.FOREIGN_KEY + simpleName + suffix).append("\", ")//
				.append(canonicalName).append(".class, ") //
				.append(idx).append(");\n");

	}

	private void addSqlStatement(final StringBuilder builder) {
		String fromColumn = SchemaConstants.FOREIGN_KEY + simpleLeftLower + SchemaConstants.FROM_SUFFIX;
		String toColumn = SchemaConstants.FOREIGN_KEY + simpleRightLower + SchemaConstants.TO_SUFFIX;

		StringBuilder statement = new StringBuilder("CREATE TABLE ");
		statement.append(tableName).append("(");
		statement.append(fromColumn).append(" Integer, ");
		statement.append(toColumn).append(" Integer, ");
		statement.append("UNIQUE(").append(fromColumn).append(", ").append(toColumn).append(") ON CONFLICT IGNORE)");

		builder.append(indent).append(TAB).append(format(CONSTANT_STRING, SchemaConstants.SQL_CREATION, statement.toString()));

		builder.append(indent).append(TAB).append(createIndexStatement(tableName, fromColumn));
		builder.append(indent).append(TAB).append(createIndexStatement(tableName, toColumn));
	}

	private String createIndexStatement(String tableName, String columnName) {
		String lowerColumnName = columnName.toLowerCase();

		StringBuilder index = new StringBuilder("CREATE INDEX ");
		index.append(tableName.toLowerCase()).append("_").append(lowerColumnName).append(SchemaConstants.INDEX_SUFFIX);
		index.append(" on ").append(tableName).append(" (");
		index.append(columnName);
		index.append(")");
		return format(CONSTANT_STRING, SchemaConstants.SQL_INDEX + "_" + lowerColumnName, index.toString());
	}

	private void addProjection(final StringBuilder builder) {
		StringBuilder projection = new StringBuilder();
		projection.append("\"").append(SchemaConstants.FOREIGN_KEY).append(simpleLeftLower + SchemaConstants.FROM_SUFFIX).append("\", ") //
				.append("\"").append(SchemaConstants.FOREIGN_KEY).append(simpleRightLower + SchemaConstants.TO_SUFFIX).append("\"");
		builder.append(indent).append(TAB).append(format(CONSTANT_STRING_ARRAY, SchemaConstants.PROJECTION, projection.toString()));
	}

	private void addAttributes(final StringBuilder builder) {
		StringBuilder concat = new StringBuilder();
		concat.append((SchemaConstants.FOREIGN_KEY + simpleLeft + SchemaConstants.FROM_SUFFIX).toUpperCase(Locale.getDefault())).append(", ") //
				.append((SchemaConstants.FOREIGN_KEY + simpleRight + SchemaConstants.TO_SUFFIX).toUpperCase(Locale.getDefault()));

		builder.append(indent).append(TAB).append(format(CONSTANT_ATTRIBUTE_ARRAY, SchemaConstants.ATTRIBUTES, concat.toString()));
	}

	private void addEnd(final StringBuilder builder) {
		builder.append(indent).append("}\n");
	}

	private String getSimpleName(final String canonical) {
		int lastDot = canonical.lastIndexOf(".");
		return canonical.substring(lastDot + 1, canonical.length());
	}
}
