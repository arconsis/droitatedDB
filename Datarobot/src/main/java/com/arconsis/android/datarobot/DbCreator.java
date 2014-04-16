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
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.arconsis.android.datarobot.hooks.DbUpdate;

/**
 * @author Falk Appel
 * @author Alexander Frank
 */
public class DbCreator extends SQLiteOpenHelper {

	private static DbCreator INSTANCE;
	private final PersitenceDefinition persistence;

	DbCreator(final Context context, final PersitenceDefinition persistence) {
		super(context, persistence.getName(), null, persistence.getVersion());
		this.persistence = persistence;
	}

	public static DbCreator getInstance(final Context context) {
		if (INSTANCE != null) {
			return INSTANCE;
		}

		INSTANCE = new DbCreator(context, PersitenceDefinition.create(context));
		return INSTANCE;
	}

	@Override
	public void onCreate(final SQLiteDatabase db) {
		for (String statement : persistence.getSqlCreationStatements()) {
			db.execSQL(statement);
		}

		for (String index : persistence.getIndexStatements()) {
			db.execSQL(index);
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
