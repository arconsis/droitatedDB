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

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.arconsis.android.datarobot.cursor.CombinedCursorImpl;
import com.arconsis.android.datarobot.schema.AbstractAttribute;
import com.arconsis.android.datarobot.schema.ToManyAssociation;
import com.arconsis.android.datarobot.schema.ToOneAssociation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.arconsis.android.datarobot.CursorOperation.tryOnCursor;
import static com.arconsis.android.datarobot.SchemaUtil.getAssociationsSchema;
import static com.arconsis.android.datarobot.SchemaUtil.getEntityInfo;
import static com.arconsis.android.datarobot.SchemaUtil.getTableName;
import static com.arconsis.android.datarobot.Utilities.getFieldValue;
import static com.arconsis.android.datarobot.Utilities.getLinkTableColumns;
import static com.arconsis.android.datarobot.Utilities.getLinkTableName;
import static com.arconsis.android.datarobot.Utilities.getPrimaryKey;
import static com.arconsis.android.datarobot.Utilities.handle;
import static com.arconsis.android.datarobot.Utilities.setFieldValue;
import static com.arconsis.android.datarobot.schema.SchemaConstants.FOREIGN_KEY;
import static com.arconsis.android.datarobot.schema.SchemaConstants.FROM_SUFFIX;
import static com.arconsis.android.datarobot.schema.SchemaConstants.TO_SUFFIX;

/**
 * Resolves entity object graphs from the db.
 *
 * @author Falk Appel
 * @author Alexander Frank
 */
class DatabaseResolver {

	private final Map<String, Object> loadedObjects;
	private final Context             context;
	private final SQLiteDatabase      database;

	public DatabaseResolver(final Context context, final SQLiteDatabase database) {
		this.context = context;
		this.database = database;
		loadedObjects = new HashMap<String, Object>();
	}

	public void resolve(final Object data, final int currentDepth, final int maxDepth) {
		// 1. load associated ids for data
		// 2. check if (Type/id) tuple is in map if yes 5. else 3.
		// 3. load object from db
		// 4. put loaded object into map
		// 5. set object to data
		// 6. resolve associations of loaded object to given depth
		if (currentDepth >= maxDepth) {
			return;
		}
		EntityData entityData = EntityData.getEntityData(data);
		if (entityData.allAssociations.size() > 0) {
			Class<?> associationsDeclaration = getAssociationsSchema(data.getClass(), context.getPackageName());
			Number id = getPrimaryKey(data, entityData);

			if (id != null) {
				loadedObjects.put("class " + data.getClass().getCanonicalName() + "#" + id, data);
				for (Field associationField : entityData.allAssociations) {
					Object declaration = getDeclaration(associationsDeclaration, associationField);
					if (declaration instanceof ToOneAssociation) {
						handleToOneAssociation(id, data, associationField, (ToOneAssociation) declaration, currentDepth, maxDepth);
					} else {
						handleToManyAssociation(id, data, associationField, (ToManyAssociation) declaration, currentDepth, maxDepth);
					}
				}
			}
		}
	}

	private void handleToOneAssociation(final Number idRequestingObject, final Object requestingObject, final Field associationField, final ToOneAssociation
			toOneAssociation, final int currentDepth, final int maxDepth) {
		String keyName = EntityData.getEntityData(requestingObject).primaryKey.getName();

		Cursor fkCursor = database.query(getTableName(requestingObject.getClass(), context.getPackageName()), new String[]{
						toOneAssociation.getAssociationAttribute().columnName()},
				keyName + " = ?", new String[]{idRequestingObject.toString()}, null, null, null);
		tryOnCursor(fkCursor, new CursorOperation<Void>() {
			@Override
			public Void execute(final Cursor cursor) throws Exception {
				if (cursor.moveToFirst() && cursor.getType(0) != Cursor.FIELD_TYPE_NULL) {
					attachAssociation(cursor.getInt(0), associationField, requestingObject, toOneAssociation, currentDepth, maxDepth);
				}
				return null;
			}
		});
	}

	private void attachAssociation(final int id, final Field associationField, final Object requestingObject, final ToOneAssociation declaration, final int
			currentDepth, final int maxDepth) {
		String mixedId = "class " + declaration.getAssociatedType().getCanonicalName() + "#" + id;
		if (loadedObjects.containsKey(mixedId)) {
			setFieldValue(associationField, requestingObject, loadedObjects.get(mixedId));
		} else {
			loadFromDatabase(id, associationField, requestingObject, declaration, currentDepth, maxDepth);
		}
	}

	private void loadFromDatabase(final int id, final Field associationField, final Object requestingObject, final ToOneAssociation declaration, final int
			currentDepth, final int maxDepth) {
		Cursor associationCursor = database.query(getTableName(declaration.getAssociatedType(), context.getPackageName()), null,
				EntityData.getEntityData(declaration.getAssociatedType()).primaryKey.getName() + "=?", new String[]{Integer.toString(id)}, null, null, null);
		tryOnCursor(associationCursor, new CursorOperation<Void>() {
			@Override
			public Void execute(final Cursor cursor) {
				if (cursor.moveToFirst()) {
					Object association = CombinedCursorImpl.create(context, cursor, getEntityInfo(declaration.getAssociatedType(), context.getPackageName()),
							declaration
									.getAssociatedType()).getCurrent();
					setFieldValue(associationField, requestingObject, association);
					loadedObjects.put(declaration.getAssociatedType().getCanonicalName() + "#" + id, association);
					resolve(association, currentDepth + 1, maxDepth);
				}
				return null;
			}
		});
	}

	private void handleToManyAssociation(final Number primaryKeyData, final Object data, final Field associationField, final ToManyAssociation toMany, final int
			currentDepth, final int maxDepth) {

		final AbstractAttribute foreignAttribute = getForeignAttribute(toMany);
		if (foreignAttribute != null) {
			EntityData entityData = EntityData.getEntityData(foreignAttribute.type());

			if (getFieldValue(data, associationField) != null) {
				for (Object object : getCollection(data, associationField)) {
					resolve(object, currentDepth + 1, maxDepth);
					loadedObjects.put(foreignAttribute.type() + "#" + getPrimaryKey(object, entityData), object);
				}
			}
			Collection<Object> target = new ArrayList<Object>();
			setFieldValue(associationField, data, target);

			List<Integer> ids = loadIdsFromLinkTable(primaryKeyData, data.getClass(), foreignAttribute, toMany);
			for (final Integer id : ids) {
				final String mixedId = foreignAttribute.type() + "#" + id;
				if (loadedObjects.containsKey(mixedId)) {
					target.add(loadedObjects.get(mixedId));
				} else {
					String primaryKeyName = entityData.primaryKey.getName();

					Cursor cursor = database.query(getTableName(foreignAttribute.type(), context.getPackageName()), null,
							primaryKeyName + "= ?", new String[]{Integer.toString(id)}, null, null, null);
					Object linkedObject = tryOnCursor(cursor, new CursorOperation<Object>() {
						@Override
						public Object execute(final Cursor cursor) {
							if (cursor.getCount() > 0) {
								Object loaded = CombinedCursorImpl.create(context, cursor, getEntityInfo(foreignAttribute.type(), context.getPackageName()),
										foreignAttribute
												.type()).getOne();
								loadedObjects.put(mixedId, loaded);
								resolve(loaded, currentDepth + 1, maxDepth);
								return loaded;
							} else {
								return null;
							}
						}
					});
					if (linkedObject != null) {
						target.add(linkedObject);
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private Collection<Object> getCollection(final Object data, final Field associationField) {
		return new ArrayList<Object>((Collection<Object>) getFieldValue(data, associationField));
	}

	private AbstractAttribute getForeignAttribute(final ToManyAssociation toMany) {
		for (AbstractAttribute attribute : getLinkTableColumns(toMany.getLinkTableSchema())) {
			if (attribute.columnName().endsWith(TO_SUFFIX)) {
				return attribute;
			}
		}
		return null;
	}

	private List<Integer> loadIdsFromLinkTable(final Number primaryKeyData, final Class<?> dataClass, final AbstractAttribute foreignAttribute, final
	ToManyAssociation toMany) {
		String tableName = getLinkTableName(toMany.getLinkTableSchema());
		String columnName = FOREIGN_KEY + dataClass.getSimpleName().toLowerCase(Locale.getDefault()) + FROM_SUFFIX;

		Cursor cursor = database.query(tableName, null, columnName + " = ?", new String[]{primaryKeyData.toString()}, null, null, null);
		return tryOnCursor(cursor, new CursorOperation<List<Integer>>() {
			@Override
			public List<Integer> execute(final Cursor cursor) throws Exception {
				LinkedList<Integer> ids = new LinkedList<Integer>();
				while (cursor.moveToNext()) {
					ids.add((Integer) foreignAttribute.getValueFromCursor(cursor));
				}
				return ids;
			}
		});
	}

	private static Object getDeclaration(final Class<?> associationsDeclaration, final Field associationField) {
		try {
			return associationsDeclaration.getField(associationField.getName().toUpperCase(Locale.getDefault())).get(null);
		} catch (Exception e) {
			throw handle(e);
		}
	}
}