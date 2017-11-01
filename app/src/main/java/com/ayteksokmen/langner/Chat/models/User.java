package com.ayteksokmen.langner.Chat.models;

import android.support.annotation.Nullable;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Author: Kartik Sharma
 * Created on: 9/1/2016 , 8:35 PM
 * Project: FirebaseChat
 */

@IgnoreExtraProperties
public class User {
    public String uid;
    public String email;
    public String firebaseToken;
    public String name;
    public String gender;
    public String photoUrl;
    public String from;
    public Boolean showMan;
    public Boolean showWoman;
    public Boolean autoShare;
    public Integer age;
    public Integer range;
    public Integer startAge;
    public Integer endAge;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public Boolean getShowMan() {
        return showMan;
    }

    public void setShowMan(Boolean showMan) {
        this.showMan = showMan;
    }

    public Boolean getShowWoman() {
        return showWoman;
    }

    public void setShowWoman(Boolean showWoman) {
        this.showWoman = showWoman;
    }

    public Boolean getAutoShare() {
        return autoShare;
    }

    public void setAutoShare(Boolean autoShare) {
        this.autoShare = autoShare;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getRange() {
        return range;
    }

    public void setRange(Integer range) {
        this.range = range;
    }

    public Integer getStartAge() {
        return startAge;
    }

    public void setStartAge(Integer startAge) {
        this.startAge = startAge;
    }

    public Integer getEndAge() {
        return endAge;
    }

    public void setEndAge(Integer endAge) {
        this.endAge = endAge;
    }

    public String getName() {
        return name;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

    public String getFirebaseToken() {
        return firebaseToken;
    }

    public User setFirebaseToken(String firebaseToken) {
        this.firebaseToken = firebaseToken;
        return this;
    }

    public User() {
    }

    public User(String uid, String name, String email, String firebaseToken, @Nullable String gender, @Nullable String photoUrl, @Nullable String from, @Nullable Boolean showMan, @Nullable Boolean showWoman, @Nullable Boolean autoShare, @Nullable Integer age, @Nullable Integer startAge, @Nullable Integer endAge, @Nullable Integer range) {
        setUid(uid);
        setName(name);
        setEmail(email);
        setFirebaseToken(firebaseToken);
        setGender(gender);
        setPhotoUrl(photoUrl);
        setFrom(from);
        setShowMan(showMan);
        setShowWoman(showWoman);
        setAutoShare(autoShare);
        setAge(age);
        setStartAge(startAge);
        setEndAge(endAge);
        setRange(range);
    }
}
