package com.arconsis.notes.db;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.droitateddb.config.Persistence;
import org.droitateddb.hooks.Create;
import org.droitateddb.hooks.DbCreate;
import org.droitateddb.hooks.DbUpdate;
import org.droitateddb.hooks.Update;

@Create
@Update
@Persistence(dbName = "notes.db", dbVersion = 1)
public class UpdateHook implements DbUpdate, DbCreate {

	@Override
	public void onUpdate(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
//		db.execSQL("DROP TABLE IF EXISTS " + DB.NoteTable.TABLE_NAME);
//		db.execSQL("DROP TABLE IF EXISTS " + DB.UserTable.TABLE_NAME);
//		db.execSQL("DROP TABLE IF EXISTS " + DB.UserNoteAssociation.TABLE_NAME);
//		db.execSQL(DB.NoteTable.SQL_CREATION);
//		db.execSQL(DB.UserTable.SQL_CREATION);
//		db.execSQL(DB.UserNoteAssociation.SQL_CREATION);

		db.execSQL("CREATE INDEX simple_idx on Simple (_id)");
		db.execSQL("CREATE INDEX note_idx on Note (_id, fk_user)");
		db.execSQL("CREATE INDEX user_idx on User (_id)");
		db.execSQL("CREATE INDEX note_user_link_idx on NoteUserLink (fk_note, fk_user)");
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.i("DB Hook", "Running create hook");
	}
}
