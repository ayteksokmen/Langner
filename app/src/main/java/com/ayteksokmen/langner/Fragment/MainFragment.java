package com.ayteksokmen.langner.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.ayteksokmen.langner.Model.Person;
import com.ayteksokmen.langner.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sem0025 on 25.09.2017.
 */

public class MainFragment extends Fragment {

	private RecyclerView lastMeet, friends, nearMe;
	private ArrayList<Person> lastMeetPeople, friendsList, nearPeople;
	private FrameLayout userView;

	public static MainFragment newInstance() {
		MainFragment mainFragment = new MainFragment();
		return mainFragment;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_main, container, false);

		lastMeet = (RecyclerView) view.findViewById(R.id.lastMeet);
		friends = (RecyclerView) view.findViewById(R.id.friends);
		nearMe = (RecyclerView) view.findViewById(R.id.nearMe);
		userView = (FrameLayout) view.findViewById(R.id.userPage);


		Person person = new Person();
		person.setName("Aytek Sökmen").setAge("24").setGender("https://cdn2.iconfinder.com/data/icons/ios-7-icons/50/user_male-512.png")
				.setId("0").setFrom("Turkey").setLearningLanguage("English").setTalkingLanguage("Türkçe").setAbout("Bilgisayar mühendisi")
				.setAvatarUrl("https://graph.facebook.com/10155697257585480/picture?type=large");

		lastMeetPeople = new ArrayList<>();
		friendsList = new ArrayList<>();
		nearPeople = new ArrayList<>();

		lastMeetPeople.add(person);
		lastMeetPeople.add(person);
		lastMeetPeople.add(person);
		lastMeetPeople.add(person);
		friendsList.add(person);
		friendsList.add(person);
		friendsList.add(person);
		nearPeople.add(person);
		nearPeople.add(person);

		lastMeet.setAdapter(new RecyclerAdapter(lastMeetPeople));
		friends.setAdapter(new RecyclerAdapter(friendsList));
		nearMe.setAdapter(new RecyclerAdapter(nearPeople));

		lastMeet.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
		friends.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
		nearMe.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

		return view;
	}

//	public void getLastChats() {
//		FirebaseDatabase.getInstance().getReference().child((Constants.ARG_CHAT_ROOMS)).addValueEventListener(new ValueEventListener() {
//			@Override
//			public void onDataChange(DataSnapshot dataSnapshot) {
//				if (dataSnapshot.hasChildren()) {
//					for (DataSnapshot child : dataSnapshot.getChildren()) {
//						if (child.getKey().contains(userId)) {
//							lastMeetPeople.add(child.getValue(Person.class));
//						}
//					}
//					matches.setLayoutManager(new GridLayoutManager(getContext(), 2));
//					matches.setAdapter(new RecyclerAdapter(matchArrayList));
//					if (matchArrayList.size() > 0) {
//						noMatch.setVisibility(View.GONE);
//						matches.setVisibility(View.VISIBLE);
//					} else {
//						noMatch.setVisibility(View.VISIBLE);
//						matches.setVisibility(View.GONE);
//					}
//				}
//			}
//
//			@Override
//			public void onCancelled(DatabaseError databaseError) {
//
//			}
//		});
//	}

//		public void getOnlineFriends() {
//		FirebaseDatabase.getInstance().getReference().child((Constants.ARG_FRIENDS)).addValueEventListener(new ValueEventListener() {
//			@Override
//			public void onDataChange(DataSnapshot dataSnapshot) {
//				if (dataSnapshot.hasChildren()) {
//					for (DataSnapshot child : dataSnapshot.getChildren()) {
//						if (child.child("lastSeen").getValue().) {
//							lastMeetPeople.add(child.getValue(Person.class));
//						}
//					}
//					matches.setLayoutManager(new GridLayoutManager(getContext(), 2));
//					matches.setAdapter(new RecyclerAdapter(matchArrayList));
//					if (matchArrayList.size() > 0) {
//						noMatch.setVisibility(View.GONE);
//						matches.setVisibility(View.VISIBLE);
//					} else {
//						noMatch.setVisibility(View.VISIBLE);
//						matches.setVisibility(View.GONE);
//					}
//				}
//			}
//
//			@Override
//			public void onCancelled(DatabaseError databaseError) {
//
//			}
//		});
//	}

	public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.feedViewHolder> {

		private List<Person> feedActivityList;

		public class feedViewHolder extends RecyclerView.ViewHolder {

			ImageView photo;
			TextView name;

			public feedViewHolder(View itemView) {
				super(itemView);
				photo = (ImageView) itemView.findViewById(R.id.avatar);
				name = (TextView) itemView.findViewById(R.id.person_name);
			}
		}

		public RecyclerAdapter(ArrayList<Person> feedActivityList) {
			this.feedActivityList = feedActivityList;
		}


		@Override
		public RecyclerAdapter.feedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			final View itemView = LayoutInflater.from(parent.getContext()).
					inflate(R.layout.item_home, parent, false);
			return new RecyclerAdapter.feedViewHolder(itemView);
		}

		@Override
		public void onBindViewHolder(RecyclerAdapter.feedViewHolder holder, final int position) {
			holder.name.setText(feedActivityList.get(position).getName());
			Picasso.with(getContext()).load(feedActivityList.get(position).getAvatarUrl()).into(holder.photo);

			holder.itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					userView.setVisibility(View.VISIBLE);
					ProfileDialog fragment = ProfileDialog.newInstance(feedActivityList.get(position).getId());
					fragment.show(getFragmentManager(), "blur_sample");
				}
			});
		}

		@Override
		public int getItemCount() {
			return feedActivityList.size();
		}
	}
}
