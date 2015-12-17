package com.arconsis.notes.ui;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.CursorAdapter;

import com.arconsis.notes.NotesApplication;
import com.arconsis.notes.db.User;
import com.arconsis.notes.generated.DB;

import org.droitateddb.BaseContentProvider;

public class NoteLoaderCallback implements LoaderCallbacks<Cursor> {

	private final Context       context;
	private final CursorAdapter cursorAdapter;
	private final int           loaderId;

	public NoteLoaderCallback(final Context context, final CursorAdapter cursorAdapter, final int loaderId) {
		this.context = context;
		this.cursorAdapter = cursorAdapter;
		this.loaderId = loaderId;
	}

	@Override
	public Loader<Cursor> onCreateLoader(final int id, final Bundle args) {
		if (loaderId == id) {
			User user = NotesApplication.get().getUser();

			return new CursorLoader(context,
			                        BaseContentProvider.uri(DB.NoteTable.TABLE_NAME),
			                        DB.NoteTable.PROJECTION,
			                        DB.NoteTable.FK_USER.columnName() + "=?",
			                        new String[]{Integer.toString(user.getId())},
			                        null);
		}
		return null;
	}

	@Override
	public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor) {
		cursorAdapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(final Loader<Cursor> loader) {
		cursorAdapter.swapCursor(null);
	}

}
