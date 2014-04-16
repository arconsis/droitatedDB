package com.arconsis.notes.db;

import java.util.Date;

import android.database.Cursor;

import com.arconsis.android.datarobot.CursorUtil;
import com.arconsis.android.datarobot.cursor.ObjectCursor;
import com.arconsis.android.datarobot.entity.AutoIncrement;
import com.arconsis.android.datarobot.entity.Column;
import com.arconsis.android.datarobot.entity.Entity;
import com.arconsis.android.datarobot.entity.PrimaryKey;
import com.arconsis.android.datarobot.entity.Relationship;

@Entity(contentProvider = true, authority = "com.arconsis.provider.notes")
public class Note {

	@Column
	@PrimaryKey
	@AutoIncrement
	private Integer _id;
	@Column
	private String title;
	@Column
	private String content;
	@Column
	private Date created;
	@Relationship
	private User user;

	public Note() {
		// default
	}

	public Note(final String title, final String content, final Date created) {
		this.title = title;
		this.content = content;
		this.created = created;
	}

	public Note(final int _id, final String title, final String content, final Date created) {
		this._id = _id;
		this.title = title;
		this.content = content;
		this.created = created;
	}

	public int getId() {
		return _id;
	}

	public String getTitle() {
		return title;
	}

	public String getContent() {
		return content;
	}

	public Date getCreated() {
		return created;
	}

	public User getUser() {
		return user;
	}

	public void setUser(final User user) {
		this.user = user;
	}

	public static Note convert(final Cursor cursor) {
		ObjectCursor<Note> cool = CursorUtil.getObjectCursor(cursor);
		return cool.getCurrent();
	}

	@Override
	public String toString() {
		return "Note(" + _id + ", " + content + ")";
	}
}
