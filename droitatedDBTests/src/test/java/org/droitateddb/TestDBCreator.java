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
import android.database.sqlite.SQLiteDatabase;

/**
 * Allows DBAccess for tests
 *
 * @author Falk Appel
 */
public class TestDBCreator extends DbCreator {

	private static PersistenceDefinition PERSISTENCE_DEFINITION;
	private        SQLiteDatabase        db;

	protected TestDBCreator(final Context context, final PersistenceDefinition persistence) {
		super(context, persistence);
	}

	public static TestDBCreator getInstance(final Context context) {
		if (PERSISTENCE_DEFINITION == null) {
			PERSISTENCE_DEFINITION = PersistenceDefinition.create(context.getApplicationContext());
		}
		return new TestDBCreator(context, PERSISTENCE_DEFINITION);
	}

	public SQLiteDatabase getTestDB() {
		return super.getDatabaseConnection();
	}

	public void tearDown() {
		super.reduceDatabaseConnection();
		if (dbConnection != null) {
			dbConnection.close();
			dbConnection = null;
		}
	}

	public SQLiteDatabase getDatabaseConnection() {
		return db;
	}

	@Override
	public void onCreate(final SQLiteDatabase db) {
		super.onCreate(db);
		this.db = db;
	}

	@Override
	public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {

	}
}
