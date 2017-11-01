package com.ayteksokmen.langner.Chat.ui.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ayteksokmen.langner.Chat.ui.fragments.UsersFragment;
import com.ayteksokmen.langner.Model.Person;

/**
 * Author: Kartik Sharma
 * Created on: 9/4/2016 , 12:03 PM
 * Project: FirebaseChat
 */

public class UserListingPagerAdapter extends FragmentPagerAdapter {
	private static final Fragment[] sFragments = new Fragment[]{
			UsersFragment.newInstance(UsersFragment.TYPE_ALL)
	};
	private static final String[] sTitles = new String[]{"Chats"};
	private Person person;


	public UserListingPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		return sFragments[position];
	}

	@Override
	public int getCount() {
		return sFragments.length;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return sTitles[position];
	}

	public Person getPerson() {
		return person;
	}

	public UserListingPagerAdapter setPerson(Person person) {
		this.person = person;
		return this;
	}
}
