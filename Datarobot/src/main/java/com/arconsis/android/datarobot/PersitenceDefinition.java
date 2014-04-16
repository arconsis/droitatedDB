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
package com.arconsis.android.datarobot;

import static com.arconsis.android.datarobot.schema.SchemaConstants.DB;
import static com.arconsis.android.datarobot.schema.SchemaConstants.DB_NAME;
import static com.arconsis.android.datarobot.schema.SchemaConstants.DB_VERSION;
import static com.arconsis.android.datarobot.schema.SchemaConstants.GENERATED_SUFFIX;
import static com.arconsis.android.datarobot.schema.SchemaConstants.LINK;
import static com.arconsis.android.datarobot.schema.SchemaConstants.SQL_CREATION;
import static com.arconsis.android.datarobot.schema.SchemaConstants.TABLE;
import static com.arconsis.android.datarobot.schema.SchemaConstants.UPDATE_HOOK;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;

import com.arconsis.android.datarobot.schema.SchemaConstants;

/**
 * @author Falk Appel
 * @author Alexander Frank
 */
final class PersitenceDefinition {

	private final String name;
	private final int version;
	private final ArrayList<String> sqlCreationStatements = new ArrayList<String>();
	private final ArrayList<String> indexStatements = new ArrayList<String>();
	private final Class<?> updateHook;

	public PersitenceDefinition(final String name, final int version, final Class<?> updateHook, final List<String> sqlCreationStatements,
			final List<String> indexStatements) {
		this.name = name;
		this.version = version;
		this.updateHook = updateHook;
		this.sqlCreationStatements.addAll(sqlCreationStatements);
		this.indexStatements.addAll(indexStatements);
	}

	public String getName() {
		return name;
	}

	public int getVersion() {
		return version;
	}

	public Class<?> getUpdateHook() {
		return updateHook;
	}

	public List<String> getSqlCreationStatements() {
		return Collections.unmodifiableList(sqlCreationStatements);
	}

	public List<String> getIndexStatements() {
		return Collections.unmodifiableList(indexStatements);
	}

	public static final PersitenceDefinition create(final Context context) {
		return loadPersistenceData(context);
	}

	private static PersitenceDefinition loadPersistenceData(final Context context) {
		try {
			Class<?> schemaClass = Class.forName(context.getPackageName() + "." + GENERATED_SUFFIX + "." + DB);
			String dbName = (String) schemaClass.getDeclaredField(DB_NAME).get(null);
			int dbVersion = (Integer) schemaClass.getDeclaredField(DB_VERSION).get(null);

			Class<?> updateHook = null;
			try {
				Field updateHookField = schemaClass.getDeclaredField(UPDATE_HOOK);
				if (updateHookField != null) {
					updateHook = Class.forName((String) updateHookField.get(null));
				}
			} catch (NoSuchFieldException e) {
				// ignore
			}

			Class<?>[] tableDefinitions = schemaClass.getDeclaredClasses();
			List<String> creationStatements = new ArrayList<String>(tableDefinitions.length);
			List<String> indexStatements = new ArrayList<String>(tableDefinitions.length);

			for (Class<?> def : tableDefinitions) {
				if (def.isInterface() && (def.getSimpleName().endsWith(TABLE) || def.getSimpleName().endsWith(LINK))) {
					String statement = (String) def.getDeclaredField(SQL_CREATION).get(null);
					creationStatements.add(statement);

					String indexing = (String) def.getDeclaredField(SchemaConstants.SQL_INDEX).get(null);
					indexStatements.add(indexing);
				}
			}

			return new PersitenceDefinition(dbName, dbVersion, updateHook, creationStatements, indexStatements);
		} catch (Exception e) {
			throw new IllegalStateException("Couldn't parse persistence data from DB class", e);
		}
	}

}
