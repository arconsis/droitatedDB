package com.arconsis.android.datarobot;

import android.database.sqlite.SQLiteDatabase;

/**
 * A function executable on an open database connection
 *
 * @author Falk Appel
 * @author Alexander Frank
 */
public interface DbFunction<T> {
	/**
	 * Execute an action on the database and return an appropriate result
	 *
	 * @param db A open database connection
	 * @return Result of your operation
	 */
	T apply(SQLiteDatabase db);
}
