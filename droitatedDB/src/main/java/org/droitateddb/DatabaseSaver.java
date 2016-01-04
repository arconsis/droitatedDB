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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import org.droitateddb.schema.SchemaConstants;

import java.lang.reflect.Field;
import java.util.*;

import static org.droitateddb.Utilities.*;

/**
 * Saves Entities to the db.
 *
 * @author Falk Appel
 * @author Alexander Frank
 */
class DatabaseSaver {

	private final Context                              context;
	private final SQLiteDatabase                       database;
	private final Map<Object, Number>                  idsInGraph;
	private final int                                  maxDepth;
	private final Collection<Object>                   newObjects;
	private final Collection<Object>                   newUnsavedObjects;
	private final Collection<ToManyUpdate>             toManyUpdates;
	private final Map<Object, Collection<ToOneUpdate>> toOneUpdaters;

	public DatabaseSaver(final Context context, final SQLiteDatabase database, final int maxDepth) {
		this.context = context;
		this.database = database;
		this.maxDepth = maxDepth;
		idsInGraph = new HashMap<Object, Number>();
		newUnsavedObjects = new HashSet<Object>();
		newObjects = new HashSet<Object>();
		toOneUpdaters = new HashMap<Object, Collection<ToOneUpdate>>();
		toManyUpdates = new ArrayList<ToManyUpdate>();
	}

	public Number save(final Object data) {
		if (idsInGraph.containsKey(data)) {
			return idsInGraph.get(data);
		}
		Number id = save(data, 0);
		performPendingToOneUpdates();
		performToManyUpdates();
		return id;
	}

	private Number save(final Object data, final int currentDepth) {
		if (idsInGraph.containsKey(data)) {
			return idsInGraph.get(data);
		}
		EntityData entityData = EntityData.getEntityData(data);
		Number id = getPrimaryKey(data, entityData);
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
			setFieldValue(entityData.primaryKey, data, castToIdType(entityData.primaryKey, id));
			// 6. deregister new now saved Object
			newUnsavedObjects.remove(data);
		}

		// last: save the to-n associated objects and update the link table
		saveToManyAssociatedObjects(data, currentDepth, entityData, id);

		return id;

	}

	private Object castToIdType(final Field primaryKey, final Number id) {
		if (primaryKey.getType().equals(Integer.class)) {
			return id.intValue();
		} else {
			return id;
		}
	}


	private void performPendingToOneUpdates() {
		for (Map.Entry<Object, Collection<ToOneUpdate>> entry : toOneUpdaters.entrySet()) {
			updateToOneAssos(entry.getKey(), entry.getValue());
		}
		toOneUpdaters.clear();
	}

	private void performToManyUpdates() {
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
					Number associationId = save(associatedObject, currentDepth + 1);
					contentValuesForeignKeys.put(SchemaConstants.FOREIGN_KEY + toOneAssociatedField.getName(), associationId.toString());
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
                new String[]{getPrimaryKey(data, entityData).toString()}, null, null, null);
        return CursorOperation.tryOnCursor(cursor, new CursorOperation<ContentValues>() {
					@Override
					public ContentValues execute(final Cursor cursor) {
						ContentValues values = new ContentValues();
						if (cursor.moveToFirst()) {
							for (int i = 0; i < cursor.getColumnCount(); i++) {
								if (!cursor.isNull(i)) {
									values.put(projection[i], cursor.getLong(i));
								}
							}
						}
						return values;
					}
				});

	}

	private void saveToManyAssociatedObjects(final Object data, final int currentDepth, final EntityData entityData, final Number id) {
		if (currentDepth >= maxDepth) {
			return;
		}

		for (Field associationField : entityData.toManyAssociations) {
			Collection<?> associatedData = getAssociatedData(data, associationField);
			Class<?> dataClass = data.getClass();
            Class<?> linkTableSchema = SchemaUtil.getToManyAsso(associationField, SchemaUtil.getAssociationsSchema(dataClass, context.getPackageName())).getLinkTableSchema();

			Set<Long> idsFromLinkTable;
			if (newObjects.contains(data)) {
				idsFromLinkTable = Collections.emptySet();
			} else {
				idsFromLinkTable = loadIdsFromLinkTable(id, linkTableSchema);
			}
			for (Object associated : associatedData) {
				if (newUnsavedObjects.contains(associated)) {
					toManyUpdates.add(new ToManyUpdate(database, id, associated, linkTableSchema, ToManyUpdate.Mode.INSERT));
				} else {
					Number associatedId = save(associated, currentDepth + 1);
					if(associatedId instanceof Integer){
						associatedId= associatedId.longValue();
					}
					if (!idsFromLinkTable.remove(associatedId)) {
						toManyUpdates.add(new ToManyUpdate(database, id, associatedId, linkTableSchema, ToManyUpdate.Mode.INSERT));
					} else {
						toManyUpdates.add(new ToManyUpdate(database, id, associatedId, linkTableSchema, ToManyUpdate.Mode.UPDATE));
					}
				}

			}
			if (!idsFromLinkTable.isEmpty()) {
				for (Number unlinkedForeignKey : idsFromLinkTable) {
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

	private Set<Long> loadIdsFromLinkTable(final Number primaryKeyData, final Class<?> linkTableSchema) {
		String tableName = getLinkTableName(linkTableSchema);
		String[] projection = getLinkTableProjection(linkTableSchema);
		Cursor cursor = database.query(
				tableName, new String[]{projection[1]}, projection[0] + " = ?", new String[]{primaryKeyData.toString()}, null, null, null);
		return CursorOperation.tryOnCursor(
				cursor, new CursorOperation<Set<Long>>() {
					@Override
					public Set<Long> execute(final Cursor cursor) {
						Set<Long> ids = new HashSet<Long>();
						while (cursor.moveToNext()) {
							ids.add(cursor.getLong(0));
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
		} else if (Boolean.class.equals(columnType) || boolean.class.equals(columnType)) {
			contentValues.put(columnName, Boolean.class.cast(columnValue) ? 1 : 0);
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

	private void update(final Number id, final Class<?> entityClass, final EntityData entityData, final ContentValues contentValues) {
		if (entityData.autoIncrement) {
			database.update(entityClass.getSimpleName(), contentValues, entityData.primaryKey.getName() + " = ?", new String[]{id.toString()});
		} else {
			database.insertWithOnConflict(entityClass.getSimpleName(), null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
		}
	}

	private long insert(final Class<?> entityClass, final ContentValues contentValues) {
		return database.insertOrThrow(entityClass.getSimpleName(), null, contentValues);
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
		private final String         firstColumn;
        private Number firstId;
		private final String         linkTableName;
		private final Mode           mode;
		private final String         secondColumn;
		private       Number         secondId;

		public ToManyUpdate(final SQLiteDatabase database, final Number id, final Number associatedId, final Class<?> linkTableSchema, final Mode mode) {
			this.database = database;
			this.mode = mode;
			linkTableName = getLinkTableName(linkTableSchema);
			String[] projection = getLinkTableProjection(linkTableSchema);
			firstColumn = projection[0];
			secondColumn = projection[1];
			firstId = id;
			secondId = associatedId;
		}

        public ToManyUpdate(final SQLiteDatabase database, final Number id, final Object associated, final Class<?> linkTableSchema, final Mode mode) {
            this(database, id, null, linkTableSchema, mode);
            this.associated = associated;
        }

        @Override
        public boolean equals(final Object o) {
            return !(o == null || !(o instanceof ToManyUpdate)) && hashCode() == o.hashCode();
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
				Number primaryKey = getPrimaryKey(associated, EntityData.getEntityData(associated));
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
					contentValues.put(firstColumn, firstId.toString());
					contentValues.put(secondColumn, secondId.toString());
					database.insertOrThrow(linkTableName, null, contentValues);
					break;

				case DELETE:
                    database.delete(linkTableName, firstColumn + "= ? AND " + secondColumn + "=?",
                            new String[]{firstId.toString(), secondId.toString()});
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