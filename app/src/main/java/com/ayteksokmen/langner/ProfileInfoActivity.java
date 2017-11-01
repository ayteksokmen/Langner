package com.ayteksokmen.langner;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.ayteksokmen.langner.Model.Person;
import com.ayteksokmen.langner.Util.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by sem0025 on 1.10.2017.
 */

public class ProfileInfoActivity extends BaseActivity {
	private DatabaseReference database;
	private String[] gender = new String[1];
	private Spinner genderSpinner, speakLang, learnLang, fromSpinner;
	private List<String> spinnerArray = new ArrayList<>();
	private String[] langArray, fromArray;
	private Context mContext;
	private String from, photoUrl, speak, learn;
	private FirebaseUser firebaseUser;

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_profile_info);
		Bundle data = getIntent().getExtras();
		photoUrl = data.getString("photoUrl");
		database = FirebaseDatabase.getInstance().getReference();
		mContext = this;
		firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
		genderSpinner = (Spinner) findViewById(R.id.gender);
		speakLang = (Spinner) findViewById(R.id.speakLang);
		learnLang = (Spinner) findViewById(R.id.learnLang);
		fromSpinner = (Spinner) findViewById(R.id.from);
		final EditText age = (EditText) findViewById(R.id.age);
		spinnerArray.add("Erkek");
		spinnerArray.add("Kadın");
		spinnerArray.add("Diğer");
		langArray = getResources().getStringArray(R.array.languages);
		fromArray = getResources().getStringArray(R.array.nationalities);
		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, spinnerArray); //selected item will look like a spinner set from XML
		ArrayAdapter<String> langArrayAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, langArray); //selected item will look like a spinner set from XML
		ArrayAdapter<String> fromArrayAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, fromArray); //selected item will look like a spinner set from XML
		spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		langArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		fromArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		genderSpinner.setAdapter(spinnerArrayAdapter);
		speakLang.setAdapter(langArrayAdapter);
		learnLang.setAdapter(langArrayAdapter);
		fromSpinner.setAdapter(fromArrayAdapter);
		findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (age.getText().toString().isEmpty() || age.getText().toString().contains("-"))
					Toast.makeText(mContext, "Lütfen geçerli bir değer giriniz.", Toast.LENGTH_SHORT).show();
				else {
					gender[0] = genderSpinner.getSelectedItem().toString();
					String cinsiyet;
					switch (gender[0]) {
						case "Erkek":
							cinsiyet = "male";
							break;
						case "Kadın":
							cinsiyet = "female";
							break;
						default:
							cinsiyet = "other";
							break;
					}

					from = fromSpinner.getSelectedItem().toString();
					speak = speakLang.getSelectedItem().toString();
					learn = learnLang.getSelectedItem().toString();

					sharedPreferences.edit().putString("userGender", cinsiyet).apply();
					sharedPreferences.edit().putString("userAge", age.getText().toString()).apply();
					sharedPreferences.edit().putString("from", from).apply();
					sharedPreferences.edit().putString("learnLang", learn).apply();
					sharedPreferences.edit().putString("speakLang", speak).apply();

					Toast.makeText(mContext, "Kaydedildi!", Toast.LENGTH_SHORT).show();

					String age1 = age.getText().toString();
					Person user = new Person(firebaseUser.getUid(),
							firebaseUser.getDisplayName(),
							firebaseUser.getEmail(),
							FirebaseInstanceId.getInstance().getToken(),
							age1,
							from,
							gender[0],
							"Henüz hakkında bir şey paylaşmamış. Çok gizemli..",  //TODO about
							"türkçe", //TODO speaking
							"ingilizce", //TODO learning
							photoUrl,
							Calendar.getInstance().getTime(),
							0.0, //TODO latitude
							0.0); //TODO longitude
					database.child(Constants.ARG_USERS)
							.child(firebaseUser.getUid())
							.setValue(user)
							.addOnCompleteListener(new OnCompleteListener<Void>() {
								@Override
								public void onComplete(@NonNull Task<Void> task) {
									if (task.isSuccessful()) {
										Toast.makeText(mContext, R.string.user_successfully_added, Toast.LENGTH_SHORT).show();

										Intent i = new Intent(ProfileInfoActivity.this, MainActivity.class);
										finish();
										startActivity(i);
									} else {
										Toast.makeText(mContext, R.string.user_unable_to_add, Toast.LENGTH_SHORT).show();
									}
								}
							});
				}
			}
		});
	}
}
