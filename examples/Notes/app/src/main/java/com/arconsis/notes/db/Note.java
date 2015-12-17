package com.arconsis.notes.db;

import android.database.Cursor;

import org.droitateddb.CursorUtil;
import org.droitateddb.cursor.ObjectCursor;
import org.droitateddb.entity.AutoIncrement;
import org.droitateddb.entity.Column;
import org.droitateddb.entity.Entity;
import org.droitateddb.entity.PrimaryKey;
import org.droitateddb.entity.Relationship;

import java.util.Date;


@Entity(contentProvider = true, authority = "org.droitateddb.provider.notes")
public class Note {

	@Column
	@PrimaryKey
	@AutoIncrement
	private Integer _id;
	@Column
	private String  title;
	@Column
	private String  content;
	@Column
	private Date    created;
	@Relationship
	private User    user;

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
