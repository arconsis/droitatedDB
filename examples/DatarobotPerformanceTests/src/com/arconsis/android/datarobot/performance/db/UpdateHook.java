package com.arconsis.android.datarobot.performance.db;

import android.database.sqlite.SQLiteDatabase;

import com.arconsis.android.datarobot.config.Persistence;
import com.arconsis.android.datarobot.hooks.DbUpdate;
import com.arconsis.android.datarobot.hooks.Update;

@Persistence(dbName = "performance.db", dbVersion = 1)
@Update
public class UpdateHook implements DbUpdate {

	@Override
	public void onUpdate(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
		// nothing to do
	}

}
