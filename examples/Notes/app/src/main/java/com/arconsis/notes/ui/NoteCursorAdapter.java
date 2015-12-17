package com.arconsis.notes.ui;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.arconsis.notes.R;
import com.arconsis.notes.generated.DB;

public class NoteCursorAdapter extends CursorAdapter {

	public NoteCursorAdapter(final Context context) {
		super(context, null, 0);
	}

	@Override
	public void bindView(final View view, final Context context, final Cursor cursor) {
		setDataToView(view, cursor);
	}

	@Override
	public View newView(final Context context, final Cursor cursor, final ViewGroup parent) {
		View view = LayoutInflater.from(context).inflate(R.layout.note, parent, false);
		setDataToView(view, cursor);
		return view;
	}

	private void setDataToView(final View view, final Cursor cursor) {

		ImageView icon = (ImageView) view.findViewById(R.id.icon);
		icon.setImageResource(R.drawable.note);

		TextView text = (TextView) view.findViewById(R.id.text);
		text.setText(cursor.getString(DB.NoteTable.TITLE.columnIndex()));
	}

}
