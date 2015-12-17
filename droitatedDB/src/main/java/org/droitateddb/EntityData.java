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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.droitateddb.entity.AutoIncrement;
import org.droitateddb.entity.Column;
import org.droitateddb.entity.PrimaryKey;
import org.droitateddb.entity.Relationship;

/**
 * @author Falk Appel
 * @author Alexander Frank
 */
final class EntityData {
	Field primaryKey;
	boolean autoIncrement;
	List<Field> columns = new ArrayList<Field>();
	List<Field> toManyAssociations = new ArrayList<Field>();
	List<Field> toOneAssociations = new ArrayList<Field>();
	List<Field> allAssociations = new ArrayList<Field>();
	Class<?> type;

	private static final ConcurrentMap<Class<?>, EntityData> ENTITY_DATA_CACHE = new ConcurrentHashMap<Class<?>, EntityData>();

	static EntityData getEntityData(final Object entity) {
		return getEntityData(entity.getClass());
	}

	static EntityData getEntityData(final Class<?> entityClass) {
		if (ENTITY_DATA_CACHE.containsKey(entityClass)) {
			return ENTITY_DATA_CACHE.get(entityClass);
		}
		EntityData entityData = new EntityData();
		entityData.type = entityClass;
		for (Field field : entityClass.getDeclaredFields()) {
			if (field.getAnnotation(Column.class) != null) {
				entityData.columns.add(field);
				if (field.getAnnotation(PrimaryKey.class) != null) {
					entityData.primaryKey = field;
					if (field.getAnnotation(AutoIncrement.class) != null) {
						entityData.autoIncrement = true;
					}
				}
			} else if (field.getAnnotation(Relationship.class) != null) {
				if (Collection.class.equals(field.getType())) {
					entityData.toManyAssociations.add(field);
				} else {
					entityData.toOneAssociations.add(field);
				}
				entityData.allAssociations.add(field);
			}
		}
		if (entityData.primaryKey == null) {
			throw new IllegalStateException("No @PrimaryKey could be found for the @Entity " + entityClass.getCanonicalName());
		}
		ENTITY_DATA_CACHE.putIfAbsent(entityClass, entityData);
		return entityData;
	}

	@Override
	public String toString() {
		return "EntityData for " + type.getCanonicalName();
	}
}