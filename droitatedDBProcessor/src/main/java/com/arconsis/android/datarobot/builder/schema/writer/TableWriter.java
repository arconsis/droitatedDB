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

import com.arconsis.android.datarobot.builder.schema.data.Association;
import com.arconsis.android.datarobot.builder.schema.data.Column;
import com.arconsis.android.datarobot.builder.schema.data.Table;
import com.arconsis.android.datarobot.schema.AssociationType;
import com.arconsis.android.datarobot.schema.SchemaConstants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import static com.arconsis.android.datarobot.builder.Constants.CONSTANT_ATTRIBUTE_ARRAY;
import static com.arconsis.android.datarobot.builder.Constants.CONSTANT_STRING;
import static com.arconsis.android.datarobot.builder.Constants.CONSTANT_STRING_ARRAY;
import static com.arconsis.android.datarobot.builder.Constants.TAB;
import static com.arconsis.android.datarobot.schema.SchemaConstants.ATTRIBUTES;
import static com.arconsis.android.datarobot.schema.SchemaConstants.CLASS_NAME;
import static com.arconsis.android.datarobot.schema.SchemaConstants.FOREIGN_KEY;
import static com.arconsis.android.datarobot.schema.SchemaConstants.INDEX_SUFFIX;
import static com.arconsis.android.datarobot.schema.SchemaConstants.PROJECTION;
import static com.arconsis.android.datarobot.schema.SchemaConstants.SQL_CREATION;
import static com.arconsis.android.datarobot.schema.SchemaConstants.SQL_INDEX;
import static com.arconsis.android.datarobot.schema.SchemaConstants.TABLE;
import static com.arconsis.android.datarobot.schema.SchemaConstants.TABLE_NAME;
import static java.lang.String.format;

/**
 * @author Alexander Frank
 * @author Falk Appel
 */
public class TableWriter implements Writer {

	private final String indent;
	private final Table  table;

	public TableWriter(final String indent, final Table table) {
		this.indent = indent;
		this.table = table;
	}

	@Override
	public String write() {
		StringBuilder builder = new StringBuilder(indent);
		List<String> columnNames = new LinkedList<String>();
		List<String> toOneAssociationNames = new LinkedList<String>();
		List<String> columnSql = new LinkedList<String>();

		addDeclaration(builder);
		addTableData(builder);
		addColumns(builder, columnSql, columnNames, toOneAssociationNames);
		addSqlStatement(builder, columnSql);
		addProjection(builder, columnNames, toOneAssociationNames);
		addAttributes(builder, columnNames);
		addAssociations(builder);
		addEnd(builder);

		return builder.toString();
	}

	private void addDeclaration(final StringBuilder builder) {
		builder.append("public interface ").append(table.getName()).append(TABLE).append(" {\n");
	}

	private void addTableData(final StringBuilder builder) {
		builder.append(indent).append(TAB).append(format(CONSTANT_STRING, CLASS_NAME, table.getEntityClassName()));
		builder.append(indent).append(TAB).append(format(CONSTANT_STRING, TABLE_NAME, table.getName()));
		builder.append("\n");
	}

	private void addColumns(final StringBuilder builder, final List<String> columnSql, final List<String> columnNames,
							final List<String> toOneAssociationNames) {
		int columnIdx = 0;
		for (Column column : table.getColumns()) {
			ColumnWriter columnWriter = new ColumnWriter(TAB, column, columnIdx++);
			builder.append(columnWriter.write());
			columnSql.add(columnWriter.getSql());
			columnNames.add(column.getNameInEntity());
		}
		for (Association association : table.getAssociations()) {
			if (association.getCardinality() == AssociationType.TO_ONE) {
				ToOneAssociationAttributeWriter toOneAssociationWriter = new ToOneAssociationAttributeWriter(TAB, association, columnIdx++);
				builder.append(toOneAssociationWriter.write());
				columnSql.add(toOneAssociationWriter.getSql());
				toOneAssociationNames.add(FOREIGN_KEY + association.getNameInEntity());
			}
		}
		builder.append("\n");
	}

	private void addSqlStatement(final StringBuilder builder, final List<String> columnSql) {
		StringBuilder statement = new StringBuilder("CREATE TABLE ");
		statement.append(table.getName()).append(" (");
		statement.append(concat(columnSql, new None()));
		statement.append(")");

		builder.append(indent).append(TAB).append(format(CONSTANT_STRING, SQL_CREATION, statement.toString()));

		for (String column : filterIndexableColumns()) {
			StringBuilder index = new StringBuilder("CREATE INDEX ");
			index.append(table.getName().toLowerCase()).append("_").append(column.toLowerCase()).append(INDEX_SUFFIX);
			index.append(" on ").append(table.getName()).append(" (");
			index.append(column);
			index.append(")");
			builder.append(indent).append(TAB).append(format(CONSTANT_STRING, SQL_INDEX + "_" + column.toUpperCase(), index.toString()));
		}
	}

	private void addProjection(final StringBuilder builder, final List<String> columnNames, final List<String> toOneAssociationNames) {
		List<String> columnsWithForeignKeys = new LinkedList<String>(columnNames);
		columnsWithForeignKeys.addAll(toOneAssociationNames);

		builder.append(indent).append(TAB).append(format(CONSTANT_STRING_ARRAY, PROJECTION, concat(columnsWithForeignKeys, new Quote())));
	}

	private void addAttributes(final StringBuilder builder, final List<String> columnNames) {
		builder.append(indent).append(TAB).append(format(CONSTANT_ATTRIBUTE_ARRAY, ATTRIBUTES, concat(columnNames, new UpperCase())));
		builder.append("\n");
	}

	private void addAssociations(final StringBuilder builder) {
		if (table.getAssociations().size() > 0) {
			builder.append(indent).append(TAB).append("public interface ").append(SchemaConstants.ASSOCIATIONS_INTERFACE).append(" {\n");
			for (Association association : table.getAssociations()) {
				AssociationsInterfaceWriter associationWriter = new AssociationsInterfaceWriter(indent + TAB + TAB, table, association);
				builder.append(associationWriter.write());
			}
			builder.append(indent).append(TAB).append("}\n");
		}
	}

	private void addEnd(final StringBuilder builder) {
		builder.append(indent).append("}\n");
	}

	private Collection<String> filterIndexableColumns() {
		Collection<String> indexable = new ArrayList<String>();
		for (Column column : table.getColumns()) {
			if (column.isPrimary()) {
				indexable.add(column.getNameInEntity());
			}
		}
		for (Association association : table.getAssociations()) {
			if (AssociationType.TO_ONE == association.getCardinality()) {
				indexable.add(FOREIGN_KEY + association.getNameInEntity());
			}
		}
		return indexable;
	}

	private String concat(final Collection<String> columns, final StringRefine refiner) {
		StringBuilder namesConcat = new StringBuilder();
		int counter = 0;
		for (String name : columns) {
			namesConcat.append(refiner.refine(name));
			if (counter++ < columns.size() - 1) {
				namesConcat.append(", ");
			}
		}
		return namesConcat.toString();
	}

	private interface StringRefine {
		String refine(String input);
	}

	private final class Quote implements StringRefine {
		@Override
		public String refine(final String input) {
			return "\"" + input + "\"";
		}
	}

	public class UpperCase implements StringRefine {
		@Override
		public String refine(final String input) {
			return input.toUpperCase(Locale.getDefault());
		}

	}

	private final class None implements StringRefine {
		@Override
		public String refine(final String input) {
			return input;
		}
	}

}
