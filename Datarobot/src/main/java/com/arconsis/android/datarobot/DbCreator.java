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
import android.database.sqlite.SQLiteOpenHelper;

import com.arconsis.android.datarobot.hooks.DbCreate;
import com.arconsis.android.datarobot.hooks.DbUpdate;

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
	private static PersistenceDefinition PERSISTENCE_DEFINITION;
	private static DbCreator             DB_CREATOR_INSTANCE;
	private static SQLiteDatabase        dbConnection;

	private final Context               context;
	private final PersistenceDefinition persistence;

	private DbCreator(final Context context, final PersistenceDefinition persistence) {
		super(context, persistence.getName(), null, persistence.getVersion());
		this.context = context;
		this.persistence = persistence;
	}

	public static DbCreator getInstance(final Context context) {
		synchronized (LOCK) {
			if (PERSISTENCE_DEFINITION == null) {
				PERSISTENCE_DEFINITION = PersistenceDefinition.create(context.getApplicationContext());
			}
			if (DB_CREATOR_INSTANCE == null) {
				DB_CREATOR_INSTANCE = new DbCreator(context, PERSISTENCE_DEFINITION);
			}
			return DB_CREATOR_INSTANCE;
		}
	}

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

	public void reduceDatabaseConnection() {
		synchronized (LOCK) {
			int numberOfOpenConnections = OPEN_CONNECTIONS.decrementAndGet();
			if (numberOfOpenConnections == 0 && dbConnection != null) {
				dbConnection.close();
				dbConnection = null;
			}
		}
	}

	public <T> T functionOnDatabase(DbFunction<T> dbFunction) {
		SQLiteDatabase db = getDatabaseConnection();
		try {
			return dbFunction.apply(db);
		} finally {
			reduceDatabaseConnection();
		}
	}

	public void consumeDatabase(DbConsumer dbConsumer) {
		SQLiteDatabase db = getDatabaseConnection();
		try {
			dbConsumer.consume(db);
		} finally {
			reduceDatabaseConnection();
		}
	}

	public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
		SQLiteDatabase db = getDatabaseConnection();
		return new DbClosingCursor(db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy), this);
	}

	public Cursor rawQuery(String sql) {
		SQLiteDatabase db = getDatabaseConnection();
		return new DbClosingCursor(db.rawQuery(sql, null), this);
	}

	@Override
	public synchronized SQLiteDatabase getReadableDatabase() {
		throw new UnsupportedOperationException("Use getDatabaseConnection and reduceDatabaseConnection or one of the performOnDatabase methods");
	}

	@Override
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
}
