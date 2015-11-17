package com.arconsis.android.datarobot;

import android.database.sqlite.SQLiteDatabase;

/**
 * Execute an action on the database
 *
 * @author Falk Appel
 * @author Alexander Frank
 */
public interface DbConsumer {
	/**
	 * Allows execution on an open database connection
	 *
	 * @param db The database connection
	 */
	void consume(SQLiteDatabase db);
}
