package com.arconsis.notes.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class LoginPreferences {

	private static final String LOGGED_IN_USER_ID = "loggedInUserId";

	public static int getSavedUserId(final Context context) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		return preferences.getInt(LOGGED_IN_USER_ID, -1);
	}

	public static void saveUserId(final Context context, final int userId) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		preferences.edit().putInt(LOGGED_IN_USER_ID, userId).apply();
	}
}
