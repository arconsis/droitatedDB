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
package org.droitateddb.test.data;

import android.database.sqlite.SQLiteDatabase;

import org.droitateddb.config.Persistence;
import org.droitateddb.hooks.Create;
import org.droitateddb.hooks.DbCreate;
import org.droitateddb.hooks.DbUpdate;
import org.droitateddb.hooks.Update;

/**
 * @author Falk Appel
 * @author Alexander Frank
 */
@Create
@Update
@Persistence(dbName = "test.db",basePackage = "org.droitateddb.test.data", dbVersion = 1)
public class UpdateHook implements DbUpdate, DbCreate {

	@Override
	public void onUpdate(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
	}
}
