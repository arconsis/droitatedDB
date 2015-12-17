package com.arconsis.notes.ui;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.arconsis.notes.NotesApplication;
import com.arconsis.notes.R;
import com.arconsis.notes.db.Note;
import com.arconsis.notes.generated.DB;
import com.arconsis.notes.preferences.LoginPreferences;
import com.arconsis.notes.ui.NoteDialog.NoteDialogAction;

import org.droitateddb.BaseContentProvider;
import org.droitateddb.DbConsumer;
import org.droitateddb.DbCreator;
import org.droitateddb.EntityService;
import org.droitateddb.FlatEntityParcelable;

public class NotesActivity extends ListActivity implements NoteDialogAction {

	private NoteCursorAdapter  noteCursorAdapter;
	private NoteLoaderCallback loaderCallback;

	private static final int NOTE_LOADER_ID      = 0;
	private static final int CREATE_NOTE_REQUEST = 0;
	private EntityService<Note> noteService;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		noteCursorAdapter = new NoteCursorAdapter(this);

		setListAdapter(noteCursorAdapter);

		loaderCallback = new NoteLoaderCallback(this, noteCursorAdapter, NOTE_LOADER_ID);
		getLoaderManager().initLoader(NOTE_LOADER_ID, null, loaderCallback);

		getListView().setFastScrollEnabled(true);
		getListView().setLongClickable(true);
		getListView().setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(final AdapterView<?> adapterView, final View view, final int postition, final long id) {
				Cursor cursor = (Cursor) noteCursorAdapter.getItem(postition);
				int noteId = cursor.getInt(DB.NoteTable._ID.columnIndex());
				if (noteService.delete(noteId)) {
					getLoaderManager().getLoader(NOTE_LOADER_ID).forceLoad();
					Toast.makeText(NotesActivity.this, R.string.delete_success, Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(NotesActivity.this, R.string.delete_fail, Toast.LENGTH_LONG).show();
				}
				return true;
			}
		});
	}

	@Override
	protected void onStart() {
		super.onStart();
		noteService = new EntityService<Note>(this, Note.class);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		noteService.close();
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		if (R.id.new_note == item.getItemId()) {
			startActivityForResult(new Intent(this, EditNoteActivity.class), CREATE_NOTE_REQUEST);
			overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			return true;
		} else if (R.id.delete_all == item.getItemId()) {
			getContentResolver().delete(BaseContentProvider.uri(Note.class.getSimpleName()), null, null);
			DbCreator.getInstance(this).consumeDatabase(new DbConsumer() {
				@Override
				public void consume(SQLiteDatabase db) {
					db.delete(DB.UserNoteAssociation.TABLE_NAME, null, null);
				}
			});
			NotesApplication.get().getUser().getNotes().clear();
			getLoaderManager().getLoader(NOTE_LOADER_ID).forceLoad();
		} else if (R.id.logout == item.getItemId()) {
			LoginPreferences.saveUserId(this, -1);
			finish();
			startActivity(new Intent(this, LoginActiviy.class));
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == CREATE_NOTE_REQUEST && resultCode == EditNoteActivity.CODE_CREATED) {
			getLoaderManager().getLoader(NOTE_LOADER_ID).forceLoad();
		}
	}

	@Override
	protected void onListItemClick(final ListView l, final View v, final int position, final long id) {
		super.onListItemClick(l, v, position, id);
		Cursor cursor = (Cursor) noteCursorAdapter.getItem(position);
		Note note = Note.convert(cursor);

		NoteDialog noteDialog = new NoteDialog();
		Bundle bundle = new Bundle();
		bundle.putParcelable(NoteDialog.ARG_NOTE, new FlatEntityParcelable<Note>(note));
		noteDialog.setArguments(bundle);
		noteDialog.show(getFragmentManager(), "note_edit");
	}

	@Override
	public void onNoteChanged(final Note note) {
		noteService.save(note, 0);
		getLoaderManager().getLoader(NOTE_LOADER_ID).forceLoad();
	}
}
