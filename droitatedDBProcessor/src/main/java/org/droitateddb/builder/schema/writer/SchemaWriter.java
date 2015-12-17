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

import org.droitateddb.builder.schema.data.Association;
import org.droitateddb.builder.schema.data.Schema;
import org.droitateddb.builder.schema.data.Table;
import org.droitateddb.schema.AssociationType;
import org.droitateddb.util.Pair;
import org.droitateddb.builder.Constants;
import org.droitateddb.schema.SchemaConstants;

import java.util.Collection;
import java.util.HashSet;

import static java.lang.String.format;

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
		builder.append(Constants.GENERATED_COMMENT);
		builder.append("package ").append(packageName).append(";\n\n");
		builder.append("import com.arconsis.android.datarobot.schema.*;\n\n");
		builder.append("public interface ").append(className).append(" {\n\n");
	}

	private void addSchemaData(final StringBuilder builder) {
		builder.append(Constants.TAB).append(String.format(Constants.CONSTANT_STRING, SchemaConstants.DB_NAME, schema.getDbName()));
		addHook(builder, SchemaConstants.CREATE_HOOK, schema.getCreateHookClassName());
		addHook(builder, SchemaConstants.UPDATE_HOOK, schema.getUpdateHookClassName());

		builder.append(Constants.TAB).append(String.format(Constants.CONSTANT_INT, SchemaConstants.DB_VERSION, schema.getDbVersion()));
		builder.append("\n");
	}

	private void addHook(StringBuilder builder, String hookName, String hookClassName) {
		if (hookClassName != null && hookClassName.length() > 0) {
			builder.append(Constants.TAB).append(String.format(Constants.CONSTANT_STRING, hookName, hookClassName));
		}
	}

	private void addTables(final StringBuilder builder, final Collection<Pair<String, String>> toNAssociations) {
		for (Table table : schema.getTables()) {
			TableWriter tableWriter = new TableWriter(Constants.TAB, table);
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
			EntityInfoWriter entityInfoWriter = new EntityInfoWriter(Constants.TAB, table);
			builder.append(entityInfoWriter.write());
		}
		builder.append("\n");
	}

	private void addToNAssociations(final StringBuilder builder, final Collection<Pair<String, String>> toNAssociations) {
		for (Pair<String, String> link : toNAssociations) {
			ToManyAssociationWriter toManyAssociationWriter = new ToManyAssociationWriter(Constants.TAB, link);
			builder.append(toManyAssociationWriter.write());
		}
	}

	private void addEnd(final StringBuilder builder) {
		builder.append("}\n");
	}

}
