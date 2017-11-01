package com.ayteksokmen.langner.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by sem0025 on 26.09.2017.
 */

public class Person implements Parcelable {
	private String id;
	private String name;
	private String email;
	private String firebaseToken;
	private String age;
	private String gender;
	private String from;
	private String talkingLanguage;
	private String learningLanguage;
	private String avatarUrl;
	private String about;
	private Date lastSeen;
	private double latitude;
	private double longitude;

	protected Person(Parcel in) {
		id = in.readString();
		name = in.readString();
		email = in.readString();
		firebaseToken = in.readString();
		age = in.readString();
		from = in.readString();
		gender = in.readString();
		avatarUrl = in.readString();
		talkingLanguage = in.readString();
		learningLanguage = in.readString();
		about = in.readString();
		lastSeen = (Date) in.readSerializable();
		latitude = in.readDouble();
		longitude = in.readDouble();
	}

	public static final Creator<Person> CREATOR = new Creator<Person>() {
		@Override
		public Person createFromParcel(Parcel in) {
			return new Person(in);
		}

		@Override
		public Person[] newArray(int size) {
			return new Person[size];
		}
	};

	public Person() {
	}

	public Person(String id, String name, String email, String firebaseToken, String age, String from, String gender, String about, String talkingLanguage, String learningLanguage, String avatarUrl, Date lastSeen, double latitude, double longitude) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.firebaseToken = firebaseToken;
		this.age = age;
		this.from = from;
		this.gender = gender;
		this.about = about;
		this.talkingLanguage = talkingLanguage;
		this.learningLanguage = learningLanguage;
		this.avatarUrl = avatarUrl;
		this.lastSeen = lastSeen;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public String getName() {
		return name;
	}

	public Person setName(String name) {
		this.name = name;
		return this;
	}

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

	public String getGender() {
		return gender;
	}

	public Person setGender(String gender) {
		this.gender = gender;
		return this;
	}

	public String getAge() {
		return age;
	}

	public Person setAge(String age) {
		this.age = age;
		return this;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeString(id);
		parcel.writeString(name);
		parcel.writeString(email);
		parcel.writeString(firebaseToken);
		parcel.writeString(age);
		parcel.writeString(from);
		parcel.writeString(gender);
		parcel.writeString(about);
		parcel.writeString(talkingLanguage);
		parcel.writeString(learningLanguage);
		parcel.writeString(avatarUrl);
		parcel.writeSerializable(lastSeen);
		parcel.writeDouble(latitude);
		parcel.writeDouble(longitude);
	}

	public String getId() {
		return id;
	}

	public Person setId(String id) {
		this.id = id;
		return this;
	}

	public String getTalkingLanguage() {
		return talkingLanguage;
	}

	public Person setTalkingLanguage(String talkingLanguage) {
		this.talkingLanguage = talkingLanguage;
		return this;
	}

	public String getLearningLanguage() {
		return learningLanguage;
	}

	public Person setLearningLanguage(String learningLanguage) {
		this.learningLanguage = learningLanguage;
		return this;
	}

	public String getFrom() {
		return from;
	}

	public Person setFrom(String from) {
		this.from = from;
		return this;
	}

	public String getAbout() {
		return about;
	}

	public Person setAbout(String about) {
		this.about = about;
		return this;
	}

	public String getEmail() {
		return email;
	}

	public Person setEmail(String email) {
		this.email = email;
		return this;
	}

	public String getFirebaseToken() {
		return firebaseToken;
	}

	public Person setFirebaseToken(String firebaseToken) {
		this.firebaseToken = firebaseToken;
		return this;
	}

	public double getLatitude() {
		return latitude;
	}

	public Person setLatitude(double latitude) {
		this.latitude = latitude;
		return this;
	}

	public double getLongitude() {
		return longitude;
	}

	public Person setLongitude(double longitude) {
		this.longitude = longitude;
		return this;
	}

	public Date getLastSeen() {
		return lastSeen;
	}

	public Person setLastSeen(Date lastSeen) {
		this.lastSeen = lastSeen;
		return this;
	}
}