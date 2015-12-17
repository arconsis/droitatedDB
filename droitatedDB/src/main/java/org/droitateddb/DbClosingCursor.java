package org.droitateddb;

import android.database.Cursor;
import android.database.CursorWrapper;

/**
 * A cursor also handling the reduction of open database connections when closed. It should be used e.g. in combination with CursorAdapter.
 *
 * @author Falk Appel
 * @author Alexander Frank
 */
class DbClosingCursor extends CursorWrapper {

	private final DbCreator dbCreator;

	public DbClosingCursor(Cursor cursor, DbCreator dbCreator) {
		super(cursor);
		this.dbCreator = dbCreator;
	}

	@Override
	public void close() {
		super.close();
		dbCreator.reduceDatabaseConnection();
	}
}
