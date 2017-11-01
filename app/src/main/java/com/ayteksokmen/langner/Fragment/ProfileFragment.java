package com.ayteksokmen.langner.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ayteksokmen.langner.R;
import com.ayteksokmen.langner.SettingsActivity;
import com.ayteksokmen.langner.Util.Constants;
import com.ayteksokmen.langner.Util.SharedPrefUtil;
import com.squareup.picasso.Picasso;

/**
 * Created by sem0025 on 25.09.2017.
 */

public class ProfileFragment extends Fragment {

	private CardView settings, message;
	private TextView profileName, ageAndFrom, about, languages, settingsText, aboutMe;
	private SharedPreferences sharedPreferences;
	private ImageView profileImage;
	private String userId;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
		userId = getArguments().getString("userId");
		View view = inflater.inflate(R.layout.fragment_profile, container, false);
		settingsText = (TextView) view.findViewById(R.id.settingsText);
		settings = (CardView) view.findViewById(R.id.settings);
		message = (CardView) view.findViewById(R.id.message);
		profileName = (TextView) view.findViewById(R.id.profile_name);
		ageAndFrom = (TextView) view.findViewById(R.id.ageandfrom);
		languages = (TextView) view.findViewById(R.id.languages);
		about = (TextView) view.findViewById(R.id.aboutText);
		aboutMe = (TextView) view.findViewById(R.id.aboutMeTV);
		profileImage = (ImageView) view.findViewById(R.id.profile_image);
		aboutMe.setText(getString(R.string.about));

		String myUserId = sharedPreferences.getString("userId", "");
		if (userId.equals(myUserId)) {
			settingsText.setText(getString(R.string.settings));
			settings.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					Intent i = new Intent(getActivity(), SettingsActivity.class);
					startActivity(i);
				}
			});
		} else {
			settingsText.setText(getString(R.string.addFriend));
			settings.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					//TODO add friend
				}
			});
			message.setVisibility(View.VISIBLE);
			message.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					//TODO chat page
				}
			});
		}


		String userId = sharedPreferences.getString("userId", "");
		String userName = sharedPreferences.getString("userName", "");
		String userEmail = sharedPreferences.getString("userEmail", "");
		String firebaseToken = new SharedPrefUtil(getContext()).getString(Constants.ARG_FIREBASE_TOKEN, null);
		String userPhotoUrl = sharedPreferences.getString("userPhotoUrl", "");
		String from = sharedPreferences.getString("from", "");
		String gender = sharedPreferences.getString("userGender", "");
		String age = sharedPreferences.getString("userAge", "");
		String aboutString = sharedPreferences.getString("about", "");
		String talkingLang = sharedPreferences.getString("speakLang", "");
		String learningLang = sharedPreferences.getString("learnLang", "");
		profileName.setText(userName);
		ageAndFrom.setText(age + ", " + from);
		languages.setText(talkingLang + " " + getString(R.string.speaking) + ", " + learningLang + " " + getString(R.string.learning) + ".");
		about.setText(aboutString);
		Picasso.with(getContext()).load(userPhotoUrl).into(profileImage);
		return view;
	}

	public static ProfileFragment newInstance(String userId) {
		Bundle bundle = new Bundle();
		bundle.putString("userId", userId);
		ProfileFragment profileFragment = new ProfileFragment();
		profileFragment.setArguments(bundle);
		return profileFragment;
	}
}
