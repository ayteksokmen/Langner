package com.ayteksokmen.langner.Fragment;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ayteksokmen.langner.Chat.ui.activities.ChatActivity;
import com.ayteksokmen.langner.Model.Friendship;
import com.ayteksokmen.langner.Model.Person;
import com.ayteksokmen.langner.R;
import com.ayteksokmen.langner.Util.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import fr.tvbarthel.lib.blurdialogfragment.SupportBlurDialogFragment;

/**
 * Created by sem0025 on 8.10.2017.
 */

public class ProfileDialog extends SupportBlurDialogFragment {

	private CardView settings, message;
	private TextView profileName, ageAndFrom, about, languages, settingsText, aboutMe;
	private SharedPreferences sharedPreferences;
	private ImageView profileImage;
	private String userId;

	public static ProfileDialog newInstance(String userId) {
		Bundle bundle = new Bundle();
		bundle.putString("userId", userId);
		ProfileDialog profileDialog = new ProfileDialog();
		profileDialog.setArguments(bundle);
		return profileDialog;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		userId = getArguments().getString("userId");

		View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_profile, null);
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
		if (!userId.equals(myUserId)) {
			isFriend(myUserId, userId);
			message.setVisibility(View.VISIBLE);
			builder.setView(view);
		}

		return builder.create();
	}


	@Override
	protected float getDownScaleFactor() {
		// Allow to customize the down scale factor.
		return 5.0f;
	}

	@Override
	protected int getBlurRadius() {
		// Allow to customize the blur radius factor.
		return 7;
	}

	@Override
	protected boolean isActionBarBlurred() {
		// Enable or disable the blur effect on the action bar.
		// Disabled by default.
		return true;
	}

	@Override
	protected boolean isDimmingEnable() {
		// Enable or disable the dimming effect.
		// Disabled by default.
		return true;
	}

	@Override
	protected boolean isRenderScriptEnable() {
		// Enable or disable the use of RenderScript for blurring effect
		// Disabled by default.
		return true;
	}

	@Override
	protected boolean isDebugEnable() {
		// Enable or disable debug mode.
		// False by default.
		return true;
	}

	private void setUser(final String uid, final HashMap<Integer, String> friendMap) {
		final Person[] user = {new Person()};
		FirebaseDatabase.getInstance()
				.getReference()
				.child(Constants.ARG_USERS)
				.child(uid)
				.addListenerForSingleValueEvent(new ValueEventListener() {
					@Override
					public void onDataChange(DataSnapshot dataSnapshot) {
						user[0] = dataSnapshot.getValue(Person.class);
						profileName.setText(user[0].getName());
						ageAndFrom.setText(user[0].getAge() + ", " + user[0].getFrom());
						languages.setText(user[0].getTalkingLanguage() + " " + getString(R.string.speaking) + ", " + user[0].getLearningLanguage() + " " + getString(R.string.learning) + ".");
						about.setText(user[0].getAbout());
						Picasso.with(getContext()).load(user[0].getAvatarUrl()).into(profileImage);

						message.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								ChatActivity.startActivity(getContext(), user[0].getName(), userId, user[0].getFirebaseToken(), user[0].getAvatarUrl());
							}
						});

						final String myUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
						if (friendMap.containsKey(0)) {
							settingsText.setText(getString(R.string.addFriend));
							settings.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View view) {
									addFriend(myUserId, userId);
								}
							});
						} else if (friendMap.containsKey(1)) {
							settingsText.setText(getString(R.string.removeFriend));
							settings.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View view) {
									removeFriend(myUserId, userId);
								}
							});
						} else {
							if (friendMap.get(0).equals(myUserId)) { //Receiver Id ben isem
								settingsText.setText(getString(R.string.dismissFriend));
								settings.setOnClickListener(new View.OnClickListener() {
									@Override
									public void onClick(View view) {
										cancelWaitFriend(myUserId, userId);
									}
								});
							} else {
								settingsText.setText(getString(R.string.applyFriend));
								settings.setOnClickListener(new View.OnClickListener() {
									@Override
									public void onClick(View view) {
										applyWaitFriend(myUserId, userId);
									}
								});
							}
						}

					}

					@Override
					public void onCancelled(DatabaseError databaseError) {

					}
				});
	}


	public void addFriend(String myUserId, String userId) {
		Friendship.FriendStatus friendStatus = Friendship.FriendStatus.WAITING;
		Friendship friendship = new Friendship().setPerson1Id(myUserId).setPerson2Id(userId);
		friendship.setFriendStatus(friendStatus);

		FirebaseDatabase.getInstance()
				.getReference()
				.child("friendship")
				.child(myUserId+"_"+userId)
				.setValue(friendship)
				.addOnCompleteListener(new OnCompleteListener<Void>() {
					@Override
					public void onComplete(@NonNull Task<Void> task) {
						if (task.isSuccessful()) {
							Toast.makeText(getContext(), "Arkadaş Eklendi.", Toast.LENGTH_SHORT).show();
							settingsText.setText(getString(R.string.dismissFriend));
						} else {
							Toast.makeText(getContext(), "Bir hata oluştu!", Toast.LENGTH_SHORT).show();
						}
					}
				});
	}

	public void removeFriend(String myUserId, String userId) {
		Friendship.FriendStatus friendStatus = Friendship.FriendStatus.NOTOK;
		Friendship friendship = new Friendship().setPerson1Id(myUserId).setPerson2Id(userId);
		friendship.setFriendStatus(friendStatus);

		FirebaseDatabase.getInstance()
				.getReference()
				.child("friendship")
				.child(myUserId+"_"+userId)
				.setValue(friendship)
				.addOnCompleteListener(new OnCompleteListener<Void>() {
					@Override
					public void onComplete(@NonNull Task<Void> task) {
						if (task.isSuccessful()) {
							Toast.makeText(getContext(), "Arkadaşlıktan Çıkarıldı.", Toast.LENGTH_SHORT).show();
							settingsText.setText(getString(R.string.addFriend));
						} else {
							Toast.makeText(getContext(), "Bir hata oluştu!", Toast.LENGTH_SHORT).show();
						}
					}
				});
	}

	public void cancelWaitFriend(String myUserId, String userId) {
		Friendship.FriendStatus friendStatus = Friendship.FriendStatus.NOTOK;
		Friendship friendship = new Friendship().setPerson1Id(myUserId).setPerson2Id(userId);
		friendship.setFriendStatus(friendStatus);

		FirebaseDatabase.getInstance()
				.getReference()
				.child("friendship")
				.child(myUserId+"_"+userId)
				.setValue(friendship)
				.addOnCompleteListener(new OnCompleteListener<Void>() {
					@Override
					public void onComplete(@NonNull Task<Void> task) {
						if (task.isSuccessful()) {
							Toast.makeText(getContext(), "Arkadaşlık İsteği İptal Edildi.", Toast.LENGTH_SHORT).show();
							settingsText.setText(getString(R.string.addFriend));
						} else {
							Toast.makeText(getContext(), "Bir hata oluştu!", Toast.LENGTH_SHORT).show();
						}
					}
				});
	}

	public void applyWaitFriend(String myUserId, String userId) {
		Friendship.FriendStatus friendStatus = Friendship.FriendStatus.OK;
		Friendship friendship = new Friendship().setPerson1Id(myUserId).setPerson2Id(userId);
		friendship.setFriendStatus(friendStatus);

		FirebaseDatabase.getInstance()
				.getReference()
				.child("friendship")
				.child(myUserId+"_"+userId)
				.setValue(friendship)
				.addOnCompleteListener(new OnCompleteListener<Void>() {
					@Override
					public void onComplete(@NonNull Task<Void> task) {
						if (task.isSuccessful()) {
							Toast.makeText(getContext(), "Arkadaşlık İsteği Kabul Edildi.", Toast.LENGTH_SHORT).show();
							settingsText.setText(getString(R.string.removeFriend));
						} else {
							Toast.makeText(getContext(), "Bir hata oluştu!", Toast.LENGTH_SHORT).show();
						}
					}
				});
	}

	public void isFriend(final String id, final String id2) {
		final String[] senderId = new String[1];
		final Map<Integer, String> friendMap = new HashMap<>();
		final Handler handler = new Handler();

		FirebaseDatabase.getInstance().getReference()
				.child(Constants.ARG_FRIENDSHIPS)
				.addListenerForSingleValueEvent(new ValueEventListener() {
					@Override
					public void onDataChange(DataSnapshot dataSnapshot) {
						for (DataSnapshot child : dataSnapshot.getChildren()) {
							if (child.getKey().contains(id) && child.getKey().contains(id2)) {
								senderId[0] = child.child("person1Id").getValue().toString();
								if (child.child("friendStatus").getValue().equals("OK")) //FRIENDS
								{
									friendMap.put(1, senderId[0]);
									handler.postDelayed(new Runnable() {
										@Override
										public void run() {
											setUser(userId, (HashMap<Integer, String>) friendMap);
										}
									}, 400);
								} else {
									friendMap.put(2, senderId[0]); //WAITING
									handler.postDelayed(new Runnable() {
										@Override
										public void run() {
											setUser(userId, (HashMap<Integer, String>) friendMap);
										}
									}, 400);
								}
							} else {
								friendMap.put(0, senderId[0]); //NOT FRIENDS
								handler.postDelayed(new Runnable() {
									@Override
									public void run() {
										setUser(userId, (HashMap<Integer, String>) friendMap);
									}
								}, 400);
							}
						}
					}

					@Override
					public void onCancelled(DatabaseError databaseError) {

					}
				});
	}
}
