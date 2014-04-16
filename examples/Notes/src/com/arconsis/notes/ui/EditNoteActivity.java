package com.arconsis.notes.ui;

import java.util.Date;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.arconsis.android.datarobot.EntityService;
import com.arconsis.notes.NotesApplication;
import com.arconsis.notes.R;
import com.arconsis.notes.db.Note;
import com.arconsis.notes.generated.DB;
import com.arconsis.notes.generated.NoteContentProvider;

public class EditNoteActivity extends Activity {
	private EditText title;
	private EditText content;
	private EntityService<Note> noteService;

	public static final int CODE_CREATED = 0;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_note);

		noteService = new EntityService<Note>(this, Note.class);

		initHome();
		initView();
	}

	private void initHome() {
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
	}

	private void initView() {
		title = (EditText) findViewById(R.id.title);
		content = (EditText) findViewById(R.id.content);
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		getMenuInflater().inflate(R.menu.edit, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		if (R.id.save_note == item.getItemId()) {
			Note newNote = new Note(title.getText().toString(), content.getText().toString(), new Date());
			NotesApplication.get().getUser().addNote(newNote);
			newNote.setUser(NotesApplication.get().getUser());
			Uri saved = NoteContentProvider.uriForItem(DB.NoteTable.TABLE_NAME, noteService.save(newNote));
			Intent result = new Intent();
			result.setData(saved);
			setResult(CODE_CREATED, result);
			finish();
			return true;
		} else if (android.R.id.home == item.getItemId()) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}
}
