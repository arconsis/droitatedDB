package com.arconsis.notes.db;

import org.droitateddb.entity.AutoIncrement;
import org.droitateddb.entity.Column;
import org.droitateddb.entity.Entity;
import org.droitateddb.entity.PrimaryKey;
import org.droitateddb.entity.Relationship;

import java.util.Collection;
import java.util.LinkedList;


@Entity
public class User {

	@PrimaryKey
	@AutoIncrement
	@Column
	private Integer _id;
	@Column
	private String  name;
	@Column
	private String  password;
	@Relationship
	private final Collection<Note> notes = new LinkedList<Note>();

	public User() {
		// default
	}

	public User(final String name, final String password) {
		this.name = name;
		this.password = password;
	}

	public Integer getId() {
		return _id;
	}

	public void setId(final Integer id) {
		_id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(final String password) {
		this.password = password;
	}

	public Collection<Note> getNotes() {
		return notes;
	}

	public void addNote(final Note note) {
		this.notes.add(note);
	}
}
