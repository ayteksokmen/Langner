package com.ayteksokmen.langner;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by sem0025 on 18.09.2017.
 */

public class BaseActivity extends AppCompatActivity {
	public SharedPreferences sharedPreferences;
	public SharedPreferences.Editor editor;

	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		editor = sharedPreferences.edit();
	}
}
