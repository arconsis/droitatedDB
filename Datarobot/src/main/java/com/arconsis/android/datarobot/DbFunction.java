package com.arconsis.android.datarobot;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by lex on 16.11.15.
 */
public interface DbFunction<T> {
	T apply(SQLiteDatabase db);
}
