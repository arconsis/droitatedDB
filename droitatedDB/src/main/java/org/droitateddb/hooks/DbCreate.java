package org.droitateddb.hooks;

import android.database.sqlite.SQLiteDatabase;

/**
 * Callback for additional initialization on the SQLite database.
 *
 * @author Alexander Frank
 * @author Falk Appel
 */
public interface DbCreate {
	/**
	 * Implement the specific db initialization in this method. <br>
	 * It will be called after the database is created.
	 *
	 * @param db Open database connection
	 */
	void onCreate(SQLiteDatabase db);
}
