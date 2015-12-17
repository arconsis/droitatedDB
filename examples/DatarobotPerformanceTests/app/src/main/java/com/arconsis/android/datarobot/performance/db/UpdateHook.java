package com.arconsis.android.datarobot.performance.db;

import android.database.sqlite.SQLiteDatabase;

import org.droitateddb.config.Persistence;
import org.droitateddb.hooks.DbUpdate;
import org.droitateddb.hooks.Update;

@Persistence(dbName = "performance.db", dbVersion = 1)
@Update
public class UpdateHook implements DbUpdate {

	@Override
	public void onUpdate(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
		// nothing to do
	}

}
