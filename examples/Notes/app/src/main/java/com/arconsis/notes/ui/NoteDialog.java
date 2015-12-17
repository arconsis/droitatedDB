package com.arconsis.notes.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.arconsis.notes.R;
import com.arconsis.notes.db.Note;

import org.droitateddb.FlatEntityParcelable;

import java.util.Date;

public class NoteDialog extends DialogFragment {

	private NoteDialogAction actions;

	public static final String ARG_NOTE = "note";

	@Override
	@SuppressWarnings("unchecked")
	public Dialog onCreateDialog(final Bundle savedInstanceState) {

		View view = getActivity().getLayoutInflater().inflate(R.layout.edit_note, null, false);

		final EditText title = (EditText) view.findViewById(R.id.title);
		final EditText content = (EditText) view.findViewById(R.id.content);

		final Note note = ((FlatEntityParcelable<Note>) getArguments().getParcelable(ARG_NOTE)).getEntity();
		title.setText(note.getTitle());
		content.setText(note.getContent());

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setView(view);

		builder.setTitle(R.string.note) //
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog, final int id) {
						actions.onNoteChanged(new Note(note.getId(), title.getText().toString(), content.getText().toString(), new Date()));
					}
				}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog, final int id) {
						// User cancelled the dialog
					}
				});
		return builder.create();
	}

	@Override
	public void onAttach(final Activity activity) {
		super.onAttach(activity);
		if (!(activity instanceof NoteDialogAction)) {
			throw new IllegalStateException("The attaching activity has to implement " + NoteDialogAction.class.getCanonicalName());
		}
		actions = (NoteDialogAction) activity;
	}

	public interface NoteDialogAction {
		void onNoteChanged(Note note);
	}
}
