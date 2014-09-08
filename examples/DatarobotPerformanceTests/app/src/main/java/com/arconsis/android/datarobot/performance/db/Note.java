package com.arconsis.android.datarobot.performance.db;

import java.util.Date;

import com.arconsis.android.datarobot.entity.AutoIncrement;
import com.arconsis.android.datarobot.entity.Column;
import com.arconsis.android.datarobot.entity.Entity;
import com.arconsis.android.datarobot.entity.PrimaryKey;
import com.arconsis.android.datarobot.entity.Relationship;

@Entity
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
		// no-args
	}

	public Note(final String title, final String content, final Date created) {
		this.title = title;
		this.content = content;
		this.created = created;
	}

	public Integer getId() {
		return _id;
	}

	public void setId(final Integer id) {
		this._id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(final String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(final String content) {
		this.content = content;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(final Date created) {
		this.created = created;
	}

	public User getUser() {
		return user;
	}

	public void setUser(final User user) {
		this.user = user;
	}
}
