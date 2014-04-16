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

import static com.arconsis.android.datarobot.builder.Constants.CONSTANT_INT;
import static com.arconsis.android.datarobot.builder.Constants.CONSTANT_STRING;
import static com.arconsis.android.datarobot.builder.Constants.GENERATED_COMMENT;
import static com.arconsis.android.datarobot.builder.Constants.TAB;
import static com.arconsis.android.datarobot.schema.SchemaConstants.DB_NAME;
import static com.arconsis.android.datarobot.schema.SchemaConstants.DB_VERSION;
import static com.arconsis.android.datarobot.schema.SchemaConstants.UPDATE_HOOK;
import static java.lang.String.format;

import java.util.Collection;
import java.util.HashSet;

import com.arconsis.android.datarobot.builder.schema.data.Association;
import com.arconsis.android.datarobot.builder.schema.data.Schema;
import com.arconsis.android.datarobot.builder.schema.data.Table;
import com.arconsis.android.datarobot.schema.AssociationType;
import com.arconsis.android.datarobot.util.Pair;

/**
 * @author Alexander Frank
 * @author Falk Appel
 */
public class SchemaWriter implements Writer {

	private final String packageName;
	private final String className;
	private final Schema schema;

	public SchemaWriter(final String packageName, final String className, final Schema schema) {
		this.packageName = packageName;
		this.className = className;
		this.schema = schema;
	}

	@Override
	public String write() {
		StringBuilder builder = new StringBuilder();
		Collection<Pair<String, String>> toNAssociations = new HashSet<Pair<String, String>>();

		addDeclaration(builder);
		addSchemaData(builder);
		addEntityInfos(builder);
		addTables(builder, toNAssociations);
		addToNAssociations(builder, toNAssociations);
		addEnd(builder);
		return builder.toString();
	}

	private void addDeclaration(final StringBuilder builder) {
		builder.append(GENERATED_COMMENT);
		builder.append("package ").append(packageName).append(";\n\n");
		builder.append("import com.arconsis.android.datarobot.schema.*;\n\n");
		builder.append("public interface ").append(className).append(" {\n\n");
	}

	private void addSchemaData(final StringBuilder builder) {
		builder.append(TAB).append(format(CONSTANT_STRING, DB_NAME, schema.getDbName()));
		String updateHookClassName = schema.getUpdateHookClassName();
		if (updateHookClassName != null && updateHookClassName.length() > 0) {
			builder.append(TAB).append(format(CONSTANT_STRING, UPDATE_HOOK, updateHookClassName));
		}
		builder.append(TAB).append(format(CONSTANT_INT, DB_VERSION, schema.getDbVersion()));
		builder.append("\n");
	}

	private void addTables(final StringBuilder builder, final Collection<Pair<String, String>> toNAssociations) {
		for (Table table : schema.getTables()) {
			TableWriter tableWriter = new TableWriter(TAB, table);
			builder.append(tableWriter.write());
			builder.append("\n");

			for (Association association : table.getAssociations()) {
				if (association.getCardinality().equals(AssociationType.TO_MANY)) {
					String tableType = table.getEntityClassName();
					String associatedType = association.getCanonicalTypeInEntity();
					toNAssociations.add(new Pair<String, String>(tableType, associatedType));
				}
			}
		}
	}

	private void addEntityInfos(final StringBuilder builder) {
		for (Table table : schema.getTables()) {
			EntityInfoWriter entityInfoWriter = new EntityInfoWriter(TAB, table);
			builder.append(entityInfoWriter.write());
		}
		builder.append("\n");
	}

	private void addToNAssociations(final StringBuilder builder, final Collection<Pair<String, String>> toNAssociations) {
		for (Pair<String, String> link : toNAssociations) {
			ToManyAssociationWriter toManyAssociationWriter = new ToManyAssociationWriter(TAB, link);
			builder.append(toManyAssociationWriter.write());
		}
	}

	private void addEnd(final StringBuilder builder) {
		builder.append("}\n");
	}

}
