package com.arconsis.notes.db;

import android.database.sqlite.SQLiteDatabase;

import com.arconsis.android.datarobot.config.Persistence;
import com.arconsis.android.datarobot.hooks.DbUpdate;
import com.arconsis.android.datarobot.hooks.Update;
import com.arconsis.notes.generated.DB;

@Update
@Persistence(dbName = "notes.db", dbVersion = 1)
public class UpdateHook implements DbUpdate {

	@Override
	public void onUpdate(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + DB.NoteTable.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + DB.UserTable.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + DB.UserNoteAssociation.TABLE_NAME);
		db.execSQL(DB.NoteTable.SQL_CREATION);
		db.execSQL(DB.UserTable.SQL_CREATION);
		db.execSQL(DB.UserNoteAssociation.SQL_CREATION);

		db.execSQL("CREATE INDEX simple_idx on Simple (_id)");
		db.execSQL("CREATE INDEX note_idx on Note (_id, fk_user)");
		db.execSQL("CREATE INDEX user_idx on User (_id)");
		db.execSQL("CREATE INDEX note_user_link_idx on NoteUserLink (fk_note, fk_user)");
	}
}
