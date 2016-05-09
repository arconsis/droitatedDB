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
package org.droitateddb;

import org.droitateddb.schema.SchemaConstants;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.droitateddb.Utilities.getDeclaredField;
import static org.droitateddb.Utilities.getStaticFieldValue;

/**
 * @author Falk Appel
 * @author Alexander Frank
 */
final class PersistenceDefinition {

    private final String name;
    private final int version;
    private final ArrayList<String> sqlCreationStatements = new ArrayList<String>();
    private final ArrayList<String> indexStatements = new ArrayList<String>();
    private final Class<?> updateHook;
    private final Class<?> createHook;

    public PersistenceDefinition(final String name, final int version, final Class<?> updateHook, final Class<?> createHook,
                                 final List<String> sqlCreationStatements, final List<String> indexStatements) {
        this.name = name;
        this.version = version;
        this.updateHook = updateHook;
        this.createHook = createHook;
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

    public Class<?> getCreateHook() {
        return createHook;
    }

    public List<String> getSqlCreationStatements() {
        return Collections.unmodifiableList(sqlCreationStatements);
    }

    public List<String> getIndexStatements() {
        return Collections.unmodifiableList(indexStatements);
    }

	public static PersistenceDefinition create(final String basePackage) {
		return loadPersistenceData(basePackage);
    }

	private static PersistenceDefinition loadPersistenceData(final String basePackage) {
        try {
			Class<?> schemaClass = Class.forName(basePackage + "." + SchemaConstants.GENERATED_SUFFIX + "." + SchemaConstants.DB);
            String dbName = getStaticFieldValue(schemaClass, SchemaConstants.DB_NAME);
            int dbVersion = getStaticFieldValue(schemaClass, SchemaConstants.DB_VERSION);

            Class<?> updateHook = getHook(schemaClass, SchemaConstants.UPDATE_HOOK);
            Class<?> createHook = getHook(schemaClass, SchemaConstants.CREATE_HOOK);

            Class<?>[] tableDefinitions = schemaClass.getDeclaredClasses();
            List<String> creationStatements = new ArrayList<String>(tableDefinitions.length);
            List<String> indexStatements = new ArrayList<String>(tableDefinitions.length);

            for (Class<?> def : tableDefinitions) {
                if (def.isInterface() && (def.getSimpleName().endsWith(SchemaConstants.TABLE) || def.getSimpleName().endsWith(SchemaConstants.LINK))) {
                    String statement = getStaticFieldValue(def,SchemaConstants.SQL_CREATION);
                    creationStatements.add(statement);

                    Field[] allFields = def.getDeclaredFields();
                    for (Field field : allFields) {
                        if (field.getName().startsWith(SchemaConstants.SQL_INDEX)) {
                            indexStatements.add((String) getStaticFieldValue(field));
                        }
                    }
                }
            }

            return new PersistenceDefinition(dbName, dbVersion, updateHook, createHook, creationStatements, indexStatements);
        } catch (Exception e) {
            throw new IllegalStateException("Couldn't parse persistence data from DB class", e);
        }
    }

    private static Class<?> getHook(Class<?> schemaClass, String hookName) {
        Class<?> hook = null;
        try {
            Field hookField = getDeclaredField(schemaClass,hookName);
            if (hookField != null) {
                String className = getStaticFieldValue(hookField);
                hook = Class.forName(className);
            }
        } catch (Exception e) {
            // ignore
        }
        return hook;
    }

}
