package com.arconsis.notes;

import android.app.Application;

import com.arconsis.notes.db.User;

public class NotesApplication extends Application {

	private User sessionUser;
	private static NotesApplication INSTANCE;

	public NotesApplication() {
		INSTANCE = this;
	}

	public User getUser() {
		return sessionUser;
	}

	public void setUser(final User user) {
		sessionUser = user;
	}

	public static NotesApplication get() {
		return INSTANCE;
	}
}
