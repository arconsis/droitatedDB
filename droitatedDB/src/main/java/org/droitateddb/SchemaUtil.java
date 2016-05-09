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

import org.droitateddb.schema.EntityInfo;
import org.droitateddb.schema.ToManyAssociation;

import java.lang.reflect.Field;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.droitateddb.Utilities.getStaticFieldValue;
import static org.droitateddb.schema.SchemaConstants.ASSOCIATIONS_INTERFACE;
import static org.droitateddb.schema.SchemaConstants.DB;
import static org.droitateddb.schema.SchemaConstants.GENERATED_SUFFIX;
import static org.droitateddb.schema.SchemaConstants.INFO_SUFFIX;
import static org.droitateddb.schema.SchemaConstants.TABLE;
import static org.droitateddb.schema.SchemaConstants.TABLE_NAME;

/**
 * Utility for different schema access.
 * 
 * @author Falk Appel
 * @author Alexander Frank
 */
class SchemaUtil {
	private static final String ASSOCIATION_TEMPLATE = "%s." + GENERATED_SUFFIX + "." + DB + "$%s" + TABLE + "$" + ASSOCIATIONS_INTERFACE;
	private static final String SCHEMA_TEMPLATE = "%s." + GENERATED_SUFFIX + "." + DB + "$%s" + TABLE;
	private static final String DB_TEMPLATE = "%s." + GENERATED_SUFFIX + "." + DB;

	private static final ConcurrentMap<Class<?>, String> TABLE_NAME_CACHE = new ConcurrentHashMap<Class<?>, String>();
	private static final ConcurrentMap<Class<?>, Class<?>> ASSOCIATION_SCHEMA_CACHE = new ConcurrentHashMap<Class<?>, Class<?>>();
	private static final ConcurrentMap<Class<?>, EntityInfo> ENTITY_INFO_CACHE = new ConcurrentHashMap<Class<?>, EntityInfo>();

	static String getTableName(final Class<?> entityClass) {
		if (TABLE_NAME_CACHE.containsKey(entityClass)) {
			return TABLE_NAME_CACHE.get(entityClass);
		}

		try {
			String className = String.format(SCHEMA_TEMPLATE, DroitatedDB.getBasePackage(), entityClass.getSimpleName());
			Class<?> schemaClass = Class.forName(className);
			String tableName = getStaticFieldValue(schemaClass,TABLE_NAME);
			TABLE_NAME_CACHE.putIfAbsent(entityClass, tableName);
			return tableName;
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	static Class<?> getAssociationsSchema(final Class<?> entityClass) {
		if (ASSOCIATION_SCHEMA_CACHE.containsKey(entityClass)) {
			ASSOCIATION_SCHEMA_CACHE.get(entityClass);
		}
		try {
			Class<?> associationsSchema = Class.forName(String.format(ASSOCIATION_TEMPLATE, DroitatedDB.getBasePackage(), entityClass.getSimpleName()));
			ASSOCIATION_SCHEMA_CACHE.putIfAbsent(entityClass, associationsSchema);
			return associationsSchema;
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException(e);
		}
	}

	static EntityInfo getEntityInfo(final Class<?> entityClass) {
		if (ENTITY_INFO_CACHE.containsKey(entityClass)) {
			return ENTITY_INFO_CACHE.get(entityClass);
		}
		try {
			Class<?> aClass = Class.forName(String.format(DB_TEMPLATE, DroitatedDB.getBasePackage()));
			EntityInfo entityInfo = getStaticFieldValue(aClass,entityClass.getSimpleName() + INFO_SUFFIX);
			ENTITY_INFO_CACHE.putIfAbsent(entityClass, entityInfo);
			return entityInfo;
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	static ToManyAssociation getToManyAsso(final Field associationField, final Class<?> associationsSchema) {
		return getStaticFieldValue(associationsSchema, associationField.getName().toUpperCase(Locale.getDefault()));
	}
}
