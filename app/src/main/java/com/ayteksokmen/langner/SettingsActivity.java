package com.ayteksokmen.langner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by sem0025 on 30.09.2017.
 */

public class SettingsActivity extends BaseActivity {
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_settings);

		Button logout = (Button) findViewById(R.id.logout);
		logout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (FirebaseAuth.getInstance().getCurrentUser() != null) {
					FirebaseAuth.getInstance().signOut();
					LoginManager.getInstance().logOut();
					sharedPreferences.edit().clear().apply();
					Intent i = new Intent(SettingsActivity.this, LoginActivity.class);
					startActivity(i);
				}
			}
		});
	}
}
