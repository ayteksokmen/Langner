package com.ayteksokmen.langner.Model;

/**
 * Created by sem0025 on 8.10.2017.
 */

public class Friendship {
	private String person1Id;
	private String person2Id;

	public enum FriendStatus {
		WAITING,
		OK,
		NOTOK
	}

	private FriendStatus friendStatus;


	public FriendStatus getFriendStatus() {
		return friendStatus;
	}

	public FriendStatus setFriendStatus(FriendStatus status) {
		friendStatus = status;
		return friendStatus;
	}

	public String getPerson1Id() {
		return person1Id;
	}

	public Friendship setPerson1Id(String person1Id) {
		this.person1Id = person1Id;
		return this;
	}

	public String getPerson2Id() {
		return person2Id;
	}

	public Friendship setPerson2Id(String person2Id) {
		this.person2Id = person2Id;
		return this;
	}
}
