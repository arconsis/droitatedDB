package com.arconsis.android.datarobot.performance.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.arconsis.android.datarobot.performance.NotesApplication;
import com.arconsis.android.datarobot.performance.db.Note;
import com.arconsis.android.datarobot.performance.db.Simple;
import com.arconsis.android.datarobot.performance.db.User;
import com.arconsis.android.datarobot.performance.generated.DB;

import org.droitateddb.BaseContentProvider;
import org.droitateddb.DbConsumer;
import org.droitateddb.DbCreator;

public class DeleteUtil {
	public static void deleteTestEntities(final Context context) {
		context.getContentResolver().delete(BaseContentProvider.uri(Note.class.getSimpleName()), null, null);
		context.getContentResolver().delete(BaseContentProvider.uri(User.class.getSimpleName()), null, null);
		context.getContentResolver().delete(BaseContentProvider.uri(Simple.class.getSimpleName()), null, null);
		DbCreator.getInstance(context).consumeDatabase(new DbConsumer() {
			@Override
			public void consume(SQLiteDatabase db) {
				db.delete(DB.UserNoteAssociation.TABLE_NAME, null, null);
			}
		});
		NotesApplication.get().getUser().getNotes().clear();
	}
}
