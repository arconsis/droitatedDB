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

import static com.arconsis.android.datarobot.builder.Constants.CONSTANT_ATTRIBUTE_ARRAY;
import static com.arconsis.android.datarobot.builder.Constants.CONSTANT_PREFIX;
import static com.arconsis.android.datarobot.builder.Constants.CONSTANT_STRING;
import static com.arconsis.android.datarobot.builder.Constants.CONSTANT_STRING_ARRAY;
import static com.arconsis.android.datarobot.builder.Constants.TAB;
import static com.arconsis.android.datarobot.schema.SchemaConstants.FOREIGN_KEY;
import static com.arconsis.android.datarobot.schema.SchemaConstants.INDEX_SUFFIX;
import static com.arconsis.android.datarobot.schema.SchemaConstants.LINK;
import static com.arconsis.android.datarobot.schema.SchemaConstants.PROJECTION;
import static com.arconsis.android.datarobot.schema.SchemaConstants.SQL_CREATION;
import static com.arconsis.android.datarobot.schema.SchemaConstants.SQL_INDEX;
import static com.arconsis.android.datarobot.schema.SchemaConstants.TABLE_NAME;
import static java.lang.String.format;

import java.util.Locale;

import com.arconsis.android.datarobot.schema.SchemaConstants;
import com.arconsis.android.datarobot.util.Pair;

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
		simpleLeft = getSimpleName(link.getFirst());
		simpleLeftLower = simpleLeft.toLowerCase(Locale.getDefault());
		canonicalRight = link.getSecond();
		simpleRight = getSimpleName(link.getSecond());
		simpleRightLower = simpleRight.toLowerCase(Locale.getDefault());

		tableName = simpleLeft + simpleRight + LINK;
	}

	@Override
	public String write() {
		StringBuilder builder = new StringBuilder();
		addDefinition(builder);
		addTableName(builder);
		addAttribute(builder, canonicalLeft, simpleLeftLower, 0);
		addAttribute(builder, canonicalRight, simpleRightLower, 1);
		builder.append("\n");
		addSqlStatment(builder);
		addProjection(builder);
		addAttributes(builder);
		addEnd(builder);
		return builder.toString();
	}

	private void addDefinition(final StringBuilder builder) {
		builder.append(indent).append("public interface ").append(simpleLeft).append(simpleRight).append(LINK).append(" {\n");
	}

	private void addTableName(final StringBuilder builder) {
		builder.append(indent).append(TAB).append(String.format(CONSTANT_STRING, TABLE_NAME, tableName));
		builder.append("\n");
	}

	private void addAttribute(final StringBuilder builder, final String canonicalName, final String simpleName, final int idx) {
		String fkToUpper = (FOREIGN_KEY + simpleName).toUpperCase(Locale.getDefault());

		builder.append(indent).append(TAB).append(CONSTANT_PREFIX).append("IntegerAttribute ").append(fkToUpper).append(" = ") //
		.append("new IntegerAttribute(") //
		.append("\"").append("\", \"") //
		.append(FOREIGN_KEY + simpleName).append("\", ")//
		.append(canonicalName).append(".class, ") //
		.append(idx).append(");\n");

	}

	private void addSqlStatment(final StringBuilder builder) {
		StringBuilder statment = new StringBuilder("CREATE TABLE ");
		statment.append(tableName).append("(");
		statment.append(FOREIGN_KEY).append(simpleLeftLower).append(" Integer, ");
		statment.append(FOREIGN_KEY).append(simpleRightLower).append(" Integer, ");
		statment.append("UNIQUE(").append(FOREIGN_KEY).append(simpleLeftLower).append(", ").append(FOREIGN_KEY).append(simpleRightLower)
		.append(") ON CONFLICT IGNORE)");

		builder.append(indent).append(TAB).append(format(CONSTANT_STRING, SQL_CREATION, statment.toString()));

		StringBuilder index = new StringBuilder("CREATE INDEX ");
		index.append(simpleLeftLower).append(simpleRightLower).append(INDEX_SUFFIX);
		index.append(" on ").append(tableName).append(" (");
		index.append(FOREIGN_KEY).append(simpleLeftLower).append(", ").append(FOREIGN_KEY).append(simpleRightLower);
		index.append(")");
		builder.append(indent).append(TAB).append(format(CONSTANT_STRING, SQL_INDEX, index.toString()));
	}

	private void addProjection(final StringBuilder builder) {
		StringBuilder projection = new StringBuilder();
		projection.append("\"").append(FOREIGN_KEY).append(simpleLeftLower).append("\", ") //
		.append("\"").append(FOREIGN_KEY).append(simpleRightLower).append("\"");
		builder.append(indent).append(TAB).append(format(CONSTANT_STRING_ARRAY, PROJECTION, projection.toString()));
	}

	private void addAttributes(final StringBuilder builder) {
		StringBuilder concat = new StringBuilder();
		concat.append((FOREIGN_KEY + simpleLeft).toUpperCase(Locale.getDefault())).append(", ") //
		.append((FOREIGN_KEY + simpleRight).toUpperCase(Locale.getDefault()));

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
