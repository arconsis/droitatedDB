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
package com.arconsis.android.datrobot.test.data;

import android.database.sqlite.SQLiteDatabase;

import com.arconsis.android.datarobot.config.Persistence;
import com.arconsis.android.datarobot.hooks.Create;
import com.arconsis.android.datarobot.hooks.DbCreate;
import com.arconsis.android.datarobot.hooks.DbUpdate;
import com.arconsis.android.datarobot.hooks.Update;

/**
 * @author Falk Appel
 * @author Alexander Frank
 */
@Create
@Update
@Persistence(dbName = "test.db", dbVersion = 1)
public class UpdateHook implements DbUpdate, DbCreate {

	@Override
	public void onUpdate(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
	}
}
