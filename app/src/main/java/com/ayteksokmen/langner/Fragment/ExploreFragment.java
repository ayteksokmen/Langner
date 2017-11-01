package com.ayteksokmen.langner.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.ayteksokmen.langner.Model.Person;
import com.ayteksokmen.langner.R;
import com.ayteksokmen.langner.Util.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sem0025 on 25.09.2017.
 */

public class ExploreFragment extends Fragment {

	private RecyclerView mSearchRecycler;
	private ImageView searchIcon;
	private Spinner ageSpinner, genderSpinner, learnSpinner, speakSpinner;
	private CardView search;
	private EditText searchText;
	private LinearLayout filterLayout;
	private FrameLayout userView, alpha;
	private ArrayList<Person> people;

	public static ExploreFragment newInstance() {
		ExploreFragment exploreFragment = new ExploreFragment();
		return exploreFragment;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_explore, container, false);
		mSearchRecycler = (RecyclerView) view.findViewById(R.id.onlineMembers);
		search = (CardView) view.findViewById(R.id.searchButton);
		searchIcon = (ImageView) view.findViewById(R.id.search);
		ageSpinner = (Spinner) view.findViewById(R.id.ageDropDown);
		userView = (FrameLayout) view.findViewById(R.id.userPage);
		alpha = (FrameLayout) view.findViewById(R.id.alpha);
		genderSpinner = (Spinner) view.findViewById(R.id.genderDropDown);
		learnSpinner = (Spinner) view.findViewById(R.id.learnDropDown);
		speakSpinner = (Spinner) view.findViewById(R.id.speakDropDown);
		filterLayout = (LinearLayout) view.findViewById(R.id.filterLayout);
		searchText = (EditText) view.findViewById(R.id.searchEditText);

		String[] ageItems = new String[]{getString(R.string.noChoose), "13 - 18", "18 - 30", "30 - 45", "45 +"};
		String[] genderItems = new String[]{getString(R.string.noChoose), getString(R.string.man), getString(R.string.woman), getString(R.string.other)};
		String[] languages = getContext().getResources().getStringArray(R.array.languages);
		languages[0] = getString(R.string.noChoose);
		ArrayAdapter<String> ageAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, ageItems);
		ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, genderItems);
		ArrayAdapter<String> learnAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, languages);
		ArrayAdapter<String> speakAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, languages);
		ageSpinner.setAdapter(ageAdapter);
		genderSpinner.setAdapter(genderAdapter);
		learnSpinner.setAdapter(learnAdapter);
		speakSpinner.setAdapter(speakAdapter);

		search.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				search();
			}
		});
		searchIcon.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (searchIcon.getTag().equals("close")) {
					filterLayout.setVisibility(View.GONE);
					searchIcon.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_search));
					ageSpinner.setSelection(0);
					genderSpinner.setSelection(0);
					learnSpinner.setSelection(0);
					speakSpinner.setSelection(0);
				} else search();
			}
		});
		searchText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (filterLayout.getVisibility() != View.VISIBLE) {
					filterLayout.setVisibility(View.VISIBLE);
					searchIcon.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_close_clear_cancel));
					searchIcon.setTag("close");
				}
			}
		});

		alpha.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				userView.setVisibility(View.GONE);
				alpha.setVisibility(View.GONE);
			}
		});


		people = new ArrayList<>();
		getAllUsers();

		return view;
	}

	private void search() {

	}

	public void getAllUsers() {
		FirebaseDatabase.getInstance().getReference().child((Constants.ARG_USERS)).addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				if (dataSnapshot.hasChildren()) {
					for (DataSnapshot child : dataSnapshot.getChildren()) {
						if (!child.getKey().contains(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
							people.add(child.getValue(Person.class));
						}
					}
					SearchRecyclerAdapter searchRecyclerAdapter = new SearchRecyclerAdapter(people);
					mSearchRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
					mSearchRecycler.setAdapter(searchRecyclerAdapter);
//					if (persons.size() > 0) {
//						noMatch.setVisibility(View.GONE);
//						matches.setVisibility(View.VISIBLE);
//					} else {
//						noMatch.setVisibility(View.VISIBLE);
//						matches.setVisibility(View.GONE);
//					}
				}
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {

			}
		});
	}

	public class SearchRecyclerAdapter extends RecyclerView.Adapter<SearchRecyclerAdapter.feedViewHolder> {

		private List<Person> feedActivityList;

		public class feedViewHolder extends RecyclerView.ViewHolder {

			ImageView photo, gender;
			TextView nameAndAge, from, talkingLang, learningLang;

			public feedViewHolder(View itemView) {
				super(itemView);
				photo = (ImageView) itemView.findViewById(R.id.avatar);
				gender = (ImageView) itemView.findViewById(R.id.gender);
				from = (TextView) itemView.findViewById(R.id.place);
				nameAndAge = (TextView) itemView.findViewById(R.id.person_name_and_age);
				talkingLang = (TextView) itemView.findViewById(R.id.talk);
				learningLang = (TextView) itemView.findViewById(R.id.learn);
			}
		}

		public SearchRecyclerAdapter(ArrayList<Person> feedActivityList) {
			this.feedActivityList = feedActivityList;
		}


		@Override
		public SearchRecyclerAdapter.feedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			final View itemView = LayoutInflater.from(parent.getContext()).
					inflate(R.layout.item_search, parent, false);
			return new SearchRecyclerAdapter.feedViewHolder(itemView);
		}

		@Override
		public void onBindViewHolder(feedViewHolder holder, final int position) {
			holder.nameAndAge.setText(feedActivityList.get(position).getName() + ", " + feedActivityList.get(position).getAge());
			holder.from.setText(feedActivityList.get(position).getFrom());
			holder.learningLang.setText(feedActivityList.get(position).getLearningLanguage() + " öğreniyor");
			holder.talkingLang.setText(feedActivityList.get(position).getTalkingLanguage() + " konuşuyor");
			Picasso.with(getContext()).load(feedActivityList.get(position).getAvatarUrl()).into(holder.photo);
			//gender e göre erkek ve kadın resmi varsayılan koy
			String gender = feedActivityList.get(position).getGender();
			if (gender.equals(getString(R.string.man)))
				holder.gender.setImageDrawable(getResources().getDrawable(R.drawable.man));
			else
				holder.gender.setImageDrawable(getResources().getDrawable(R.drawable.girl));

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
