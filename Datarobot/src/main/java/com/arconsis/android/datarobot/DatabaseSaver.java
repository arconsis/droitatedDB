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

import static com.arconsis.android.datarobot.SchemaUtil.getAssociationsSchema;
import static com.arconsis.android.datarobot.SchemaUtil.getToManyAsso;
import static com.arconsis.android.datarobot.Utilities.getFieldValue;
import static com.arconsis.android.datarobot.Utilities.getLinkTableName;
import static com.arconsis.android.datarobot.Utilities.getLinkTableProjection;
import static com.arconsis.android.datarobot.Utilities.getPrimaryKey;
import static com.arconsis.android.datarobot.Utilities.setFieldValue;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.arconsis.android.datarobot.schema.SchemaConstants;

/**
 * Saves Entities to the db.
 * 
 * @author Falk Appel
 * @author Alexander Frank
 * 
 */
class DatabaseSaver {

	private final Context context;
	private final SQLiteDatabase database;
	private final Map<Object, Integer> idsInGraph;
	private final int maxDepth;
	private final Collection<Object> newObjects;
	private final Collection<Object> newUnsavedObjects;
	private final Collection<ToManyUpdate> toManyUpdates;
	private final Map<Object, Collection<ToOneUpdate>> toOneUpdaters;

	public DatabaseSaver(final Context context, final SQLiteDatabase database, final int maxDepth) {
		this.context = context;
		this.database = database;
		this.maxDepth = maxDepth;
		idsInGraph = new HashMap<Object, Integer>();
		newUnsavedObjects = new HashSet<Object>();
		newObjects = new HashSet<Object>();
		toOneUpdaters = new HashMap<Object, Collection<ToOneUpdate>>();
		toManyUpdates = new ArrayList<ToManyUpdate>();
	}

	public int save(final Object data) {
		if (idsInGraph.containsKey(data)) {
			return idsInGraph.get(data);
		}
		int id = save(data, 0);
		performPendingToOneUpdates();
		perfomToManyUpdates();
		return id;
	}

	private int save(final Object data, final int currentDepth) {
		if (idsInGraph.containsKey(data)) {
			return idsInGraph.get(data);
		}
		EntityData entityData = EntityData.getEntityData(data);

		Integer id = getPrimaryKey(data, entityData);
		if (id != null) {
			// 1. put id into object graph
			idsInGraph.put(data, id);
			// 2. collect to-1 associations values and save the associated objects
			ContentValues contentValuesForeignKeys = collectToOneAssociatedValuesAndSaveAssociatedObjects(data, entityData, currentDepth);
			// 3. collect normal columns of data
			ContentValues contentValuesEntity = getFieldContent(data, entityData);
			if (contentValuesForeignKeys.size() > 0) {
				contentValuesEntity.putAll(contentValuesForeignKeys);
			}
			// 4. update db
			update(id, data.getClass(), entityData, contentValuesEntity);
		} else {
			if (!entityData.autoIncrement) {
				throw new IllegalStateException("PrimaryKey must not be null since " + entityData.type.getName()
						+ " specifies @AutoIncrement. Object has no PrimaryKey: " + data.toString());
			}
			// 1. register new unsaved Object
			newUnsavedObjects.add(data);
			newObjects.add(data);
			// 2. collect to-1 associations values and save the associated objects
			ContentValues contentValuesForeignKeys = collectToOneAssociatedValuesAndSaveAssociatedObjects(data, entityData, currentDepth);
			// 3. collect normal columns of data
			ContentValues contentValuesEntity = getFieldContent(data, entityData);
			if (contentValuesForeignKeys.size() > 0) {
				contentValuesEntity.putAll(contentValuesForeignKeys);
			}
			// 4. insert into db
			id = insert(data.getClass(), contentValuesEntity);
			// 5. put id into object graph
			idsInGraph.put(data, id);
			setFieldValue(entityData.primaryKey, data, id);
			// 6. deregister new now saved Object
			newUnsavedObjects.remove(data);
		}

		// last: save the to-n associated objects and update the link table
		saveToManyAssociatedObjects(data, currentDepth, entityData, id);

		return id;

	}

	private void performPendingToOneUpdates() {
		for (Map.Entry<Object, Collection<ToOneUpdate>> entry : toOneUpdaters.entrySet()) {
			updateToOneAssos(entry.getKey(), entry.getValue());
		}
		toOneUpdaters.clear();
	}

	private void perfomToManyUpdates() {
		Set<ToManyUpdate> toUpdate = new HashSet<ToManyUpdate>();
		for (ToManyUpdate toManyUpdate : toManyUpdates) {
			toManyUpdate.loadIdIfNecessary();
			toUpdate.add(toManyUpdate);
		}
		for (ToManyUpdate toManyUpdate : toUpdate) {
			toManyUpdate.perform();
		}
		toManyUpdates.clear();
	}

	private ContentValues collectToOneAssociatedValuesAndSaveAssociatedObjects(final Object data, final EntityData entityData, final int currentDepth) {
		ContentValues contentValuesForeignKeys = new ContentValues(0);
		if (currentDepth >= maxDepth) {
			if (entityData.autoIncrement) {
				return contentValuesForeignKeys;
			} else {
				return resolveExistingForeignKeyValues(data, entityData);
			}
		}
		for (Field toOneAssociatedField : entityData.toOneAssociations) {
			toOneAssociatedField.setAccessible(true);
			Object associatedObject = getFieldValue(data, toOneAssociatedField);
			if (associatedObject != null) {
				if (newUnsavedObjects.contains(associatedObject)) {
					Collection<ToOneUpdate> collection = toOneUpdaters.get(data);
					if (collection == null) {
						collection = new ArrayList<DatabaseSaver.ToOneUpdate>();
						toOneUpdaters.put(data, collection);
					}
					collection.add(new ToOneUpdate(SchemaConstants.FOREIGN_KEY + toOneAssociatedField.getName(), associatedObject));
				} else {
					int associationId = save(associatedObject, currentDepth + 1);
					contentValuesForeignKeys.put(SchemaConstants.FOREIGN_KEY + toOneAssociatedField.getName(), associationId);
				}
			} else {
				contentValuesForeignKeys.putNull(SchemaConstants.FOREIGN_KEY + toOneAssociatedField.getName());
			}
		}
		return contentValuesForeignKeys;
	}

	private ContentValues resolveExistingForeignKeyValues(final Object data, final EntityData entityData) {
		if (entityData.toOneAssociations.isEmpty()) {
			return new ContentValues();
		}
		String tableName = SchemaUtil.getTableName(entityData.type, context.getPackageName());
		final String[] projection = new String[entityData.toOneAssociations.size()];
		int i = 0;
		for (Field toOneAssociatedField : entityData.toOneAssociations) {
			projection[i++] = SchemaConstants.FOREIGN_KEY + toOneAssociatedField.getName();
		}

		Cursor cursor = database.query(tableName, projection, entityData.primaryKey.getName() + " = ?",
				new String[] { Integer.toString(getPrimaryKey(data, entityData)) }, null, null, null);
		return CursorOperation.tryOnCursor(cursor, new CursorOperation<ContentValues>() {
			@Override
			public ContentValues execute(final Cursor cursor) {
				ContentValues values = new ContentValues();
				if (cursor.moveToFirst()) {
					for (int i = 0; i < cursor.getColumnCount(); i++) {
						if (!cursor.isNull(i)) {
							values.put(projection[i], cursor.getInt(i));
						}
					}
				}
				return values;
			}
		});

	}

	private void saveToManyAssociatedObjects(final Object data, final int currentDepth, final EntityData entityData, final Integer id) {
		if (currentDepth >= maxDepth) {
			return;
		}

		for (Field associationField : entityData.toManyAssociations) {
			Collection<?> associatedData = getAssociatedData(data, associationField);
			Class<?> dataClass = data.getClass();
			Class<?> linkTableSchema = getToManyAsso(associationField, getAssociationsSchema(dataClass, context.getPackageName())).getLinkTableSchema();

			Set<Integer> idsFromLinkTable = null;
			if (newObjects.contains(data)) {
				idsFromLinkTable = Collections.emptySet();
			} else {
				idsFromLinkTable = loadIdsFromLinkTable(id, linkTableSchema);
			}
			for (Object associated : associatedData) {
				if (newUnsavedObjects.contains(associated)) {
					toManyUpdates.add(new ToManyUpdate(database, id, associated, linkTableSchema, ToManyUpdate.Mode.INSERT));
				} else {
					Integer associatedId = save(associated, currentDepth + 1);
					if (!idsFromLinkTable.remove(associatedId)) {
						toManyUpdates.add(new ToManyUpdate(database, id, associatedId, linkTableSchema, ToManyUpdate.Mode.INSERT));
					} else {
						toManyUpdates.add(new ToManyUpdate(database, id, associatedId, linkTableSchema, ToManyUpdate.Mode.UPDATE));
					}
				}

			}
			if (!idsFromLinkTable.isEmpty()) {
				for (Integer unlinkedForeignKey : idsFromLinkTable) {
					ToManyUpdate toManyUpdateDelete = new ToManyUpdate(database, id, unlinkedForeignKey, linkTableSchema, ToManyUpdate.Mode.DELETE);
					if (!toManyUpdates.contains(toManyUpdateDelete)) {
						toManyUpdates.add(toManyUpdateDelete);
					}

				}
			}
		}
	}

	private Collection<?> getAssociatedData(final Object data, final Field associationField) {
		associationField.setAccessible(true);
		Object association = getFieldValue(data, associationField);
		if (association != null) {
			return (Collection<?>) getFieldValue(data, associationField);
		} else {
			return Collections.emptyList();
		}
	}

	private ContentValues getFieldContent(final Object data, final EntityData entityData) {
		ContentValues contentValuesEntity = new ContentValues(entityData.columns.size());
		for (Field column : entityData.columns) {
			put(contentValuesEntity, column, data);
		}
		return contentValuesEntity;
	}

	private Set<Integer> loadIdsFromLinkTable(final int primaryKeyData, final Class<?> linkTableSchema) {
		String tableName = getLinkTableName(linkTableSchema);
		String[] projection = getLinkTableProjection(linkTableSchema);
		Cursor cursor = database.query(tableName, new String[] { projection[1] }, projection[0] + " = ?", new String[] { Integer.toString(primaryKeyData) },
				null, null, null);
		return CursorOperation.tryOnCursor(cursor, new CursorOperation<Set<Integer>>() {
			@Override
			public Set<Integer> execute(final Cursor cursor) {
				Set<Integer> ids = new HashSet<Integer>();
				while (cursor.moveToNext()) {
					ids.add(cursor.getInt(0));
				}
				return ids;
			}
		});
	}

	private void put(final ContentValues contentValues, final Field column, final Object data) {
		column.setAccessible(true);
		Class<?> columnType = column.getType();
		String columnName = column.getName();
		Object columnValue = getFieldValue(data, column);

		if (columnValue == null) {
			contentValues.putNull(columnName);
		} else if (String.class.equals(columnType)) {
			contentValues.put(columnName, String.class.cast(columnValue));
		} else if (Integer.class.equals(columnType) || int.class.equals(columnType)) {
			contentValues.put(columnName, Integer.class.cast(columnValue));
		} else if (Float.class.equals(columnType) || float.class.equals(columnType)) {
			contentValues.put(columnName, Float.class.cast(columnValue));
		} else if (Double.class.equals(columnType) || double.class.equals(columnType)) {
			contentValues.put(columnName, Double.class.cast(columnValue));
		} else if (Long.class.equals(columnType) || long.class.equals(columnType)) {
			contentValues.put(columnName, Long.class.cast(columnValue));
		} else if (byte[].class.equals(columnType)) {
			contentValues.put(columnName, byte[].class.cast(columnValue));
		} else if (Date.class.equals(columnType)) {
			contentValues.put(columnName, Date.class.cast(columnValue).getTime());
		}
	}

	private void updateToOneAssos(final Object key, final Collection<ToOneUpdate> updates) {
		ContentValues values = new ContentValues();
		for (ToOneUpdate toOneUpdate : updates) {
			values.putAll(toOneUpdate.getContentValues());
		}
		EntityData entityData = EntityData.getEntityData(key);
		update(getPrimaryKey(key, entityData), key.getClass(), entityData, values);
	}

	private void update(final Integer id, final Class<? extends Object> entityClass, final EntityData entityData, final ContentValues contentValues) {
		if (entityData.autoIncrement) {
			database.update(entityClass.getSimpleName(), contentValues, entityData.primaryKey.getName() + " = ?", new String[] { Integer.toString(id) });
		} else {
			database.insertWithOnConflict(entityClass.getSimpleName(), null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
		}
	}

	private int insert(final Class<? extends Object> entityClass, final ContentValues contentValues) {
		return (int) database.insertOrThrow(entityClass.getSimpleName(), null, contentValues);
	}

	private static final class ToOneUpdate {

		private final Object associatedObject;
		private final String foreignkey;

		ToOneUpdate(final String foreignkey, final Object associatedObject) {
			this.foreignkey = foreignkey;
			this.associatedObject = associatedObject;
		}

		ContentValues getContentValues() {
			ContentValues contentValuesForeignKey = new ContentValues(1);
			contentValuesForeignKey.put(foreignkey, getPrimaryKey(associatedObject, EntityData.getEntityData(associatedObject)).toString());
			return contentValuesForeignKey;
		}
	}

	private static final class ToManyUpdate {
		private enum Mode {
			DELETE, INSERT, UPDATE
		}

		private Object associated;
		private final SQLiteDatabase database;
		private final String firstColumn;
		private Integer firstId;
		private final String linkTableName;
		private final Mode mode;
		private final String secondColumn;
		private Integer secondId;

		public ToManyUpdate(final SQLiteDatabase database, final Integer id, final Integer associatedId, final Class<?> linkTableSchema, final Mode mode) {
			this.database = database;
			this.mode = mode;
			linkTableName = getLinkTableName(linkTableSchema);
			String[] projection = getLinkTableProjection(linkTableSchema);
			firstColumn = projection[0];
			secondColumn = projection[1];
			firstId = id;
			secondId = associatedId;
		}

		public ToManyUpdate(final SQLiteDatabase database, final Integer id, final Object associated, final Class<?> linkTableSchema, final Mode mode) {
			this(database, id, null, linkTableSchema, mode);
			this.associated = associated;
		}

		@Override
		public boolean equals(final Object o) {
			if (o == null || !(o instanceof ToManyUpdate)) {
				return false;
			}
			return hashCode() == o.hashCode();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((this.firstId == null) ? 0 : this.firstId.intValue());
			result = prime * result + ((this.linkTableName == null) ? 0 : this.linkTableName.hashCode());
			result = prime * result + ((this.secondId == null) ? 0 : this.secondId.intValue());
			return result;
		}

		public void loadIdIfNecessary() {
			if (associated != null) {
				Integer primaryKey = getPrimaryKey(associated, EntityData.getEntityData(associated));
				if (firstId == null) {
					firstId = primaryKey;
				} else {
					secondId = primaryKey;
				}
			}
		}

		public void perform() {
			switch (mode) {
				case INSERT:
					ContentValues contentValues = new ContentValues(2);
					contentValues.put(firstColumn, firstId);
					contentValues.put(secondColumn, secondId);
					database.insertOrThrow(linkTableName, null, contentValues);
					break;

				case DELETE:
					database.delete(linkTableName, firstColumn + "= ? AND " + secondColumn + "=?",
							new String[] { Integer.toString(firstId), Integer.toString(secondId) });
					break;
				case UPDATE:
					// Everything is fine, nothing to do.
					break;
				default:
					throw new IllegalStateException("Unsupported case: " + mode);
			}

		}
	}
}