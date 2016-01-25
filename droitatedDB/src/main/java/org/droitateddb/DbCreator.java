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
import android.database.sqlite.SQLiteOpenHelper;

import org.droitateddb.hooks.DbCreate;
import org.droitateddb.hooks.DbUpdate;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Handles db creation and updates.
 *
 * @author Falk Appel
 * @author Alexander Frank
 */
public class DbCreator extends SQLiteOpenHelper {

	private static final Object        LOCK             = new Object();
	private static final AtomicInteger OPEN_CONNECTIONS = new AtomicInteger(0);

	private static          PersistenceDefinition PERSISTENCE_DEFINITION;
	private static          DbCreator             DB_CREATOR_INSTANCE;
	protected static volatile SQLiteDatabase        dbConnection;
	private static String basePackage;

	private final PersistenceDefinition persistence;

	protected DbCreator(final Context context, final PersistenceDefinition persistence) {
		super(context, persistence.getName(), null, persistence.getVersion());
		this.persistence = persistence;
	}

	public static DbCreator getInstance(final Context context) {
		synchronized (LOCK) {
			if (PERSISTENCE_DEFINITION == null) {
				basePackage = context.getApplicationContext().getPackageName();
				PERSISTENCE_DEFINITION = PersistenceDefinition.create(basePackage);
			}
			if (DB_CREATOR_INSTANCE == null) {
				DB_CREATOR_INSTANCE = new DbCreator(context, PERSISTENCE_DEFINITION);
			}
			return DB_CREATOR_INSTANCE;
		}
	}

	/**
	 * Gives you a database connection you can access the database with.<br>
	 * Don't forget to call reduceDatabaseConnection when you are done with the connection.
	 *
	 * @return Connection to the local SQLite database
	 */
	public SQLiteDatabase getDatabaseConnection() {
		synchronized (LOCK) {
			if (dbConnection == null) {
				OPEN_CONNECTIONS.set(0);
				dbConnection = super.getWritableDatabase();
			}
			OPEN_CONNECTIONS.incrementAndGet();
			return dbConnection;
		}
	}

	/**
	 * Reduce the connection opened. This should always be called after calling getDatabaseConnection and only after that.
	 */
	public void reduceDatabaseConnection() {
		synchronized (LOCK) {
			int numberOfOpenConnections = OPEN_CONNECTIONS.decrementAndGet();
			if (numberOfOpenConnections == 0 && dbConnection != null) {
				dbConnection.close();
				dbConnection = null;
			}
		}
	}

	/**
	 * Execute a function on the database, to return some values. The opening and closing of the database connection is handled for you.<br>
	 * Note if you want to query a cursor from the database consider using the {@code DbCreator#query} or {@code DbCreator#rawQuery} method.<br>
	 * This can be useful when working with {@code CursorAdapter}
	 *
	 * @param <T>        Resulting type
	 * @param dbFunction Function you want to execute on the database
	 * @return The result of the function call
	 */
	public <T> T functionOnDatabase(DbFunction<T> dbFunction) {
		SQLiteDatabase db = getDatabaseConnection();
		try {
			return dbFunction.apply(db);
		} finally {
			reduceDatabaseConnection();
		}
	}

	/**
	 * Execute an action on the database. The opening and closing of the database connection is handled for you.
	 *
	 * @param dbConsumer Action you want to execute on the database
	 */
	public void consumeDatabase(DbConsumer dbConsumer) {
		SQLiteDatabase db = getDatabaseConnection();
		try {
			dbConsumer.consume(db);
		} finally {
			reduceDatabaseConnection();
		}
	}

	/**
	 * Executes a query on the database. Note that when calling close on the returned cursor the database connection count is reduces as well.
	 *
	 * @param table         Name of the table
	 * @param columns       Used columns of the queried table
	 * @param selection     Selection statement
	 * @param selectionArgs Selection arguments
	 * @param groupBy       SQL group by
	 * @param having        SQL having
	 * @param orderBy       SQL order by
	 * @return A Cursor containing the result, which reduces the database connection count when closed
	 */
	public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
		SQLiteDatabase db = getDatabaseConnection();
		return new DbClosingCursor(db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy), this);
	}

	/**
	 * Executes a raw query on the database. Note that when calling close on the returned cursor the database connection count is reduces as well.
	 *
	 * @param sql Raw SQL statement
	 * @return A Cursor containing the result, which reduces the database connection count when closed
	 */
	public Cursor rawQuery(String sql) {
		SQLiteDatabase db = getDatabaseConnection();
		return new DbClosingCursor(db.rawQuery(sql, null), this);
	}

	/**
	 * This method is not supported with this helper. You have to use getDatabaseConnection and reduceDatabaseConnection
	 *
	 * @throws UnsupportedOperationException Use the getDatabaseConnection method and the reduceDatabaseConnection method to access the {@code SQLiteDatabase}
	 * @deprecated Don't use this method any more. Use getDatabaseConnection.
	 */
	@Override
	@Deprecated
	public synchronized SQLiteDatabase getReadableDatabase() {
		throw new UnsupportedOperationException("Use getDatabaseConnection and reduceDatabaseConnection or one of the performOnDatabase methods");
	}

	/**
	 * This method is not supported with this helper. You have to use getDatabaseConnection and reduceDatabaseConnection
	 *
	 * @throws UnsupportedOperationException Use the getDatabaseConnection method and the reduceDatabaseConnection method to access the {@code SQLiteDatabase}
	 * @deprecated Don't use this method any more. Use getDatabaseConnection.
	 */
	@Override
	@Deprecated
	public synchronized SQLiteDatabase getWritableDatabase() {
		throw new UnsupportedOperationException("Use getDatabaseConnection and reduceDatabaseConnection or one of the performOnDatabase methods");
	}

	@Override
	public void onCreate(final SQLiteDatabase db) {
		for (String statement : persistence.getSqlCreationStatements()) {
			db.execSQL(statement);
		}

		for (String index : persistence.getIndexStatements()) {
			db.execSQL(index);
		}

		Class<?> createHook = persistence.getCreateHook();
		if (createHook != null) {
			try {
				DbCreate createHookInstance = (DbCreate) createHook.newInstance();
				createHookInstance.onCreate(db);
			} catch (Exception e) {
				throw new IllegalStateException("Couldn't invoke the create hook", e);
			}
		}
	}

	@Override
	public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
		Class<?> updateHook = persistence.getUpdateHook();
		if (updateHook != null) {
			try {
				DbUpdate updater = (DbUpdate) updateHook.newInstance();
				updater.onUpdate(db, oldVersion, newVersion);
			} catch (Exception e) {
				throw new IllegalStateException("Couldn't invoke the update hook", e);
			}
		}
	}

	public static String getBasePackage(){
		return basePackage;
	}
}
