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
package org.droitateddb.hooks;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Callback for updating the SQLite database on version change.
 *
 * @author Alexander Frank
 * @author Falk Appel
 */
public interface DbUpdate {
	/**
	 * Implement the specific db update in this method. <br>
	 * It will be called within the {@link SQLiteOpenHelper#onUpgrade(SQLiteDatabase, int, int)} method.
	 *
	 * @param db         Open database connection
	 * @param oldVersion old version migrating from
	 * @param newVersion new version migrating to
	 */
	void onUpdate(final SQLiteDatabase db, final int oldVersion, final int newVersion);
}
