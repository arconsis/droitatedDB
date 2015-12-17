package com.arconsis.android.datarobot.performance.tasks;

import android.content.Context;

import com.arconsis.android.datarobot.performance.NotesApplication;
import com.arconsis.android.datarobot.performance.db.Note;
import com.arconsis.android.datarobot.performance.db.User;

import org.droitateddb.EntityService;

import java.util.Date;

public final class NoteCreationTask extends AbstractCreationTask {
	private final User user = NotesApplication.get().getUser();
	private final EntityService<Note> noteService;

	public NoteCreationTask(final Context context, final CreationFinishedListener listener) {
		super(context, listener);
		noteService = new EntityService<Note>(context, Note.class);
	}

	@Override
	protected void createSingle(final int iter) {
		Note note = new Note("Note #" + iter, "This is the " + (iter + 1) + " note created in this cycle", new Date());
		note.setUser(user);
		user.addNote(note);
		noteService.save(note);
	}

	@Override
	protected int times() {
		return 100;
	}

	@Override
	protected String dialogMsg() {
		return "Creating " + times() + " notes, which are saved individually. All notes are related to a single user => Note n:1 User";
	}
}
