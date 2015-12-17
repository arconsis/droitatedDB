package com.arconsis.android.datarobot.performance;

import android.app.Application;

import com.arconsis.android.datarobot.performance.db.User;

import org.droitateddb.EntityService;

import java.util.List;

public class NotesApplication extends Application {

	private User sessionUser;
	private static NotesApplication INSTANCE;

	@Override
	public void onCreate() {
		super.onCreate();

		INSTANCE = this;
		EntityService<User> entityService = new EntityService<User>(this, User.class);
		List<User> users = entityService.get();

		if (users.size() == 0) {
			sessionUser = new User("The global user");
		} else {
			sessionUser = users.get(0);
			entityService.resolveAssociations(sessionUser);
		}
		entityService.close();

		INSTANCE = this;
	}

	public User getUser() {
		return sessionUser;
	}

	public static NotesApplication get() {
		return INSTANCE;
	}
}
