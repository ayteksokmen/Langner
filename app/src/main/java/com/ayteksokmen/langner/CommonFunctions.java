package com.ayteksokmen.langner;

import com.ayteksokmen.langner.Util.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sem0025 on 1.10.2017.
 */

public class CommonFunctions {

	public static Map<Integer, String> isFriend(final String id, final String id2) {
		final String[] senderId = new String[1];
		final Map<Integer, String> friendMap = new HashMap<>();
		FirebaseDatabase.getInstance().getReference()
				.child(Constants.ARG_FRIENDSHIPS)
				.addListenerForSingleValueEvent(new ValueEventListener() {
					@Override
					public void onDataChange(DataSnapshot dataSnapshot) {
						if (dataSnapshot.child(Constants.ARG_FRIENDSHIPS).exists() && dataSnapshot.child(Constants.ARG_FRIENDSHIPS).hasChildren()) {
							for (DataSnapshot child : dataSnapshot.child(Constants.ARG_FRIENDSHIPS).getChildren()) {
								if (child.getKey().contains(id) && child.getKey().contains(id2)) {
									senderId[0] = child.child("senderId").getValue().toString();
									if (child.child("status").getValue().equals("OK")) //FRIENDS
									{
										friendMap.put(1, senderId[0]);
									} else {
										friendMap.put(2, senderId[0]); //WAITING
									}
								} else friendMap.put(0, senderId[0]); //NOT FRIENDS
							}
						} else friendMap.put(-1, "-1");
					}

					@Override
					public void onCancelled(DatabaseError databaseError) {

					}
				});
		return friendMap;
	}

}
