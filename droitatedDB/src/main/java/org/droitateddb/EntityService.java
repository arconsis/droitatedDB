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

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import org.droitateddb.cursor.CombinedCursorImpl;
import org.droitateddb.entity.Column;
import org.droitateddb.entity.Entity;
import org.droitateddb.entity.PrimaryKey;
import org.droitateddb.schema.EntityInfo;
import org.droitateddb.validation.AccumulatedValidationResult;
import org.droitateddb.validation.InvalidEntityException;
import org.droitateddb.validation.ValidationToggle;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Provides support for executing CRUD operations on {@link org.droitateddb.entity.Entity} classes, such getting them from the database or
 * saving them to the database.<br>
 * <br>
 * Each operation opens a connection to the {@link SQLiteDatabase}. If you want only one db connection use
 * {@link ConnectedEntityService}
 *
 * @param <E> Entity, for which this service will be used
 * @author Falk Appel
 * @author Alexander Frank
 */
public class EntityService<E> {
    private final Context context;
    private final Class<E> entityClass;
    private String tableName;
    private final EntityInfo entityInfo;
    private Field primaryKey;
    protected DbCreator dbCreator;
    private DatabaseValidator<E> databaseValidator;
    private ValidationToggle toggle;

    /**
     * Creates a {@link EntityService} for the given {@link org.droitateddb.entity.Entity}.<br>
     * The validation when saving {@link org.droitateddb.entity.Entity}s is turned on.
     *
     * @param context     Android context
     * @param entityClass Class of the {@link org.droitateddb.entity.Entity}, the service should be used for
     * @throws IllegalArgumentException When the given {@link #entityClass} is no {@link org.droitateddb.entity.Entity}
     */
    public EntityService(final Context context, final Class<E> entityClass) {
        this(context, entityClass, ValidationToggle.ON, DbCreator.getInstance(context));
    }

    /**
     * Creates a {@link EntityService} for the given {@link org.droitateddb.entity.Entity}
     *
     * @param context     Android context
     * @param entityClass Class of the {@link org.droitateddb.entity.Entity}, the service should be used for
     * @param toggle      Controls the use of validation on saving  {@link org.droitateddb.entity.Entity}s
     * @throws IllegalArgumentException When the given {@link #entityClass} is no {@link org.droitateddb.entity.Entity}
     */
    public EntityService(final Context context, final Class<E> entityClass, ValidationToggle toggle) {
        this(context, entityClass, toggle, DbCreator.getInstance(context));
    }

    /*
     * Provides test access to constructor
     */
    EntityService(final Context context, final Class<E> entityClass, ValidationToggle toggle, final DbCreator dbCreator) {
        if (entityClass.getAnnotation(Entity.class) == null) {
            throw new IllegalArgumentException("The EntityService can only be used for @Entity annotated classes");
        }
        this.context = context;
        this.entityClass = entityClass;
        this.entityInfo = SchemaUtil.getEntityInfo(entityClass);
        this.tableName = SchemaUtil.getTableName(entityClass);
        this.toggle = toggle;
        this.dbCreator = dbCreator;
        this.databaseValidator = new DatabaseValidator<E>();
        initColumns();
    }

    private void initColumns() {
        for (Field field : entityClass.getDeclaredFields()) {
            if (field.getAnnotation(Column.class) != null) {
                if (field.getAnnotation(PrimaryKey.class) != null) {
                    primaryKey = field;
                }
            }
        }
    }

    /**
     * Reads all {@link Entity}s of the service specific type from the database
     *
     * @return All {@link Entity}s stored in the database, if non are found a empty list is returned
     */
    public List<E> get() {
        return find(null, null, null);
    }

    /**
     * Gets a specific {@link Entity} according to the given id
     *
     * @param id primary key of the {@link Entity}
     * @return The {@link Entity} to the given id, or null if non was found
     */
    public E get(final long id) {
        SQLiteDatabase database = openDB();
        try {
            Cursor cursor = database.query(tableName, null, primaryKey.getName() + " = ?", new String[]{Long.toString(id)}, null, null, null);
            if (cursor.getCount() == 0) {
                return null;
            }
            return CursorOperation.tryOnCursor(cursor, new CursorOperation<E>() {
                @Override
                public E execute(final Cursor cursor) {
                    return CombinedCursorImpl.create(context, cursor, entityInfo, entityClass).getCurrent();
                }
            });
        } finally {
            closeDB(database);
        }
    }

    protected void closeDB(SQLiteDatabase database) {
        dbCreator.reduceDatabaseConnection();
    }

    protected SQLiteDatabase openDB() {
        return dbCreator.getDatabaseConnection();
    }

    /**
     * Search for {@link Entity}s in the database with basic SQL WHERE statements.
     *
     * @param selection     SQL WHERE statement
     * @param selectionArgs Arguments for the WHERE statement
     * @param order         Sort order of the result
     * @return All found {@link Entity}s or an empty list of non are found
     */
    public List<E> find(final String selection, final String[] selectionArgs, final String order) {
        SQLiteDatabase database = openDB();
        try {
            Cursor cursor = database.query(tableName, null, selection, selectionArgs, null, null, order);
            return CursorOperation.tryOnCursor(cursor, new CursorOperation<List<E>>() {
                @Override
                public List<E> execute(final Cursor cursor) {
                    return new ArrayList<E>(CombinedCursorImpl.create(context, cursor, entityInfo, entityClass).getAll());
                }
            });
        } finally {
            closeDB(database);
        }
    }

    /**
     * Resolves the associations to a given {@link Entity} object and all underlying associations within the object
     * graph.
     *
     * @param data {@link Entity} on which the associations should be resolved
     */
    public void resolveAssociations(final E data) {
        SQLiteDatabase database = openDB();
        try {
            new DatabaseResolver(context, database).resolve(data, 0, Integer.MAX_VALUE);
        } finally {
            closeDB(database);
        }
    }

    /**
     * Resolves the associations to a given {@link Entity} object. The associations will only be resolved to the given
     * depth within the object graph.
     *
     * @param data     {@link Entity} on which the associations should be resolved
     * @param maxDepth The maximum depth to which the associations should be resolved
     */
    public void resolveAssociations(final E data, final int maxDepth) {
        SQLiteDatabase database = openDB();
        try {
            new DatabaseResolver(context, database).resolve(data, 0, maxDepth);
        } finally {
            closeDB(database);
        }
    }

    /**
     * Stores the given {@link Entity} to the database. All attached associations will be stored as well.
     *
     * @param data {@link Entity} which should be stored
     * @return The primary key of the given data.
     * @throws IllegalStateException                             When the {@link PrimaryKey} field and its value of the {@link Entity} could
     *                                                           not be determined
     * @throws org.droitateddb.validation.InvalidEntityException when the given entity is invalid.
     */
    public long save(final E data) {
        return save(data, Integer.MAX_VALUE);
    }

    /**
     * Stores the given {@link Entity} to the database. Associated objects will be saved to the given maxDepth.<br>
     * maxDepth of 0 means that only the given {@link Entity} itself will be saved without associations.
     *
     * @param data     {@link Entity} which should be stored
     * @param maxDepth The maximum depth of the associated objects which should also be saved.
     * @return The primary key of the given data.
     * @throws IllegalStateException                             When the {@link PrimaryKey} field and its value of the {@link Entity} could
     *                                                           not be determined
     * @throws org.droitateddb.validation.InvalidEntityException when the given entity is invalid.
     */
    public long save(final E data, final int maxDepth) {
        if (toggle == ValidationToggle.ON) {
            AccumulatedValidationResult validationResult = databaseValidator.validate(data, maxDepth);
            if (!validationResult.isValid()) {
                throw new InvalidEntityException(validationResult);
            }
        }

        final SQLiteDatabase database = openDB();
        try {
            return transactional(database, new DatabaseOperation<Number>() {
                @Override
                public Number execute() {
                    return new DatabaseSaver(database, maxDepth).save(data);
                }
            }).longValue();
        } finally {
            closeDB(database);
        }
    }

    /**
     * Stores all given {@link Entity}s in the {@link Collection} to the database. All attached associations will be
     * stored as well.
     *
     * @param data {@link Entity}s which should be stored
     * @throws IllegalStateException                             When the {@link PrimaryKey} field and its value of the {@link Entity} could
     *                                                           not be determined
     * @throws org.droitateddb.validation.InvalidEntityException when at least one of the given entities is invalid.
     */
    public void save(final Collection<E> data) {
        save(data, Integer.MAX_VALUE);
    }

    /**
     * Stores all given {@link Entity}s in the {@link Collection} to the database. Associated objects will be saved to
     * the given maxDepth.<br>
     * maxDepth of 0 means that only the given {@link Entity} itself will be saved without associations.
     *
     * @param data     {@link Entity} which should be stored
     * @param maxDepth The maximum depth of the associated objects which should also be saved.
     * @throws IllegalStateException                             When the {@link PrimaryKey} field and its value of the {@link Entity} could
     *                                                           not be determined
     * @throws org.droitateddb.validation.InvalidEntityException when at least one of the given entities is invalid.
     */
    public void save(final Collection<E> data, final int maxDepth) {
        if (toggle == ValidationToggle.ON) {
            AccumulatedValidationResult validationResult = databaseValidator.validate(data, maxDepth);
            if (!validationResult.isValid()) {
                throw new InvalidEntityException(validationResult);
            }
        }

        SQLiteDatabase database = openDB();
        try {
            final DatabaseSaver databaseSaver = new DatabaseSaver(database, maxDepth);
            transactional(database, new DatabaseOperation<Void>() {
                @Override
                public Void execute() {
                    for (E object : data) {
                        databaseSaver.save(object);
                    }
                    return null;
                }
            });
        } finally {
            closeDB(database);
        }
    }

    /**
     * Deletes the given {@link Entity} from the database
     *
     * @param data {@link Entity} that should be deleted
     * @return <code>true</code> if the {@link Entity} could be deleted, <code>false</code> otherwise
     * @throws IllegalStateException    When the {@link PrimaryKey} field and its value of the {@link Entity} could not be determined
     * @throws IllegalArgumentException When the value of the {@link PrimaryKey} field is null
     */
    public boolean delete(final E data) {
        Number id = (Number) Utilities.getFieldValue(data, primaryKey);
        if (id == null) {
            throw new IllegalArgumentException("The @PrimaryKey of the given @Entity can not be null");
        }
        return delete(id.longValue());
    }

    /**
     * Deletes the data of the {@link Entity} for the given id
     *
     * @param id primary key of the {@link Entity} to be deleted
     * @return <code>true</code> if the {@link Entity} could be deleted, <code>false</code> otherwise
     */
    public boolean delete(final long id) {
        SQLiteDatabase database = openDB();
        try {
            int delete = database.delete(tableName, EntityData.getEntityData(entityClass).primaryKey.getName() + "= ?", new String[]{Long.toString(id)});
            return delete == 1;
        } finally {
            closeDB(database);
        }
    }

    private <T> T transactional(SQLiteDatabase database, final DatabaseOperation<T> operation) {
        database.beginTransaction();
        try {
            T result = operation.execute();
            database.setTransactionSuccessful();
            return result;
        } finally {
            database.endTransaction();
        }
    }

    private static interface DatabaseOperation<E> {
        public E execute();
    }

    /**
     * Will be removed with Version 0.2.0 <br>
     * Use {@link ConnectedEntityService} if you need only one db connection.
     */
    // TODO:remove with Version 0.2.0
    @Deprecated
    public void close() {

    }

}
