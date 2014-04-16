package com.arconsis.notes.ui;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.arconsis.android.datarobot.EntityService;
import com.arconsis.notes.NotesApplication;
import com.arconsis.notes.R;
import com.arconsis.notes.db.User;
import com.arconsis.notes.generated.DB;
import com.arconsis.notes.preferences.LoginPreferences;

public class LoginActiviy extends Activity {

	private EditText user;
	private EditText password;
	private EntityService<User> userService;
	private CheckBox stayLoggedIn;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		userService = new EntityService<User>(this, User.class);

		int id = LoginPreferences.getSavedUserId(this);
		if (id > -1) {
			finish();
			overridePendingTransition(0, 0);
			login(userService.get(id));
		}

		setContentView(R.layout.login);

		user = (EditText) findViewById(R.id.user);
		password = (EditText) findViewById(R.id.password);
		stayLoggedIn = (CheckBox) findViewById(R.id.stay_logged_in);

		findViewById(R.id.login).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				if (checkLoginData()) {
					List<User> foundUsers = findUserByName();
					if (foundUsers.size() == 0) {
						Toast.makeText(LoginActiviy.this, "User not found", Toast.LENGTH_LONG).show();
						return;
					}
					User found = foundUsers.get(0);
					if (found.getPassword().equals(password.getText().toString())) {
						login(found);
					} else {
						Toast.makeText(LoginActiviy.this, "Wrong password", Toast.LENGTH_LONG).show();
					}
				}
			}
		});
		findViewById(R.id.create).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				if (checkLoginData()) {
					List<User> found = findUserByName();
					if (found.size() > 0) {
						Toast.makeText(LoginActiviy.this, "User already exists", Toast.LENGTH_LONG).show();
						return;
					}
					User created = new User(user.getText().toString(), password.getText().toString());
					userService.save(created);
					login(created);
				}
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		userService.close();
	}

	private List<User> findUserByName() {
		return userService.find(DB.UserTable.NAME.columnName() + "=?", new String[] { user.getText().toString() }, null);
	}

	private boolean checkLoginData() {
		String userName = user.getText().toString();
		String pass = password.getText().toString();

		if (userName == null || userName.length() == 0) {
			Toast.makeText(this, "Enter a user name", Toast.LENGTH_LONG).show();
			return false;
		}

		if (pass == null || pass.length() == 0) {
			Toast.makeText(this, "Enter a password", Toast.LENGTH_LONG).show();
			return false;
		}

		return true;
	}

	private void login(final User user) {
		if (stayLoggedIn != null && stayLoggedIn.isChecked()) {
			LoginPreferences.saveUserId(this, user.getId());
		}

		NotesApplication.get().setUser(user);
		startActivity(new Intent(LoginActiviy.this, NotesActivity.class));
	}
}
