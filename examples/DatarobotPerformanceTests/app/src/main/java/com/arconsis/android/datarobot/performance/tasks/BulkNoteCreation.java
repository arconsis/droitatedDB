package com.arconsis.android.datarobot.performance.tasks;

import android.content.Context;

import com.arconsis.android.datarobot.performance.NotesApplication;
import com.arconsis.android.datarobot.performance.db.Note;
import com.arconsis.android.datarobot.performance.db.User;

import org.droitateddb.EntityService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class BulkNoteCreation extends AbstractCreationTask {

	private final EntityService<Note> noteService;
	private final User user = NotesApplication.get().getUser();

	private static final int NOTES_PER_BULK = 10000;

	public BulkNoteCreation(final Context context, final CreationFinishedListener listener) {
		super(context, listener);
		noteService = new EntityService<Note>(context, Note.class);
	}

	@Override
	protected void createSingle(final int iter) {
		List<Note> bulk = new ArrayList<Note>(NOTES_PER_BULK);
		for (int i = 0; i < NOTES_PER_BULK; i++) {
			Note note = new Note("Note #" + i, "This is the " + (i + 1) + " note created in this cycle", new Date());
			note.setUser(user);
			user.addNote(note);
			bulk.add(note);
		}
		noteService.save(bulk);
	}

	@Override
	protected int times() {
		return 1;
	}

	@Override
	protected String dialogMsg() {
		return "Creating " + NOTES_PER_BULK + " notes with a bulk operation. All notes are related to a single user => Note n:1 User";
	}

}