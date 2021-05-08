package com.example.quizzyapplication_v2.Online;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class User implements Parcelable {
    private String username;
    private String email;
    private int exp;
    private int level;
    private String profileImage;

    public User(String username, String email, int exp, int level, String profileImage) {
        this.username = username;
        this.email = email;
        this.exp = exp;
        this.level = level;
        this.profileImage = profileImage;
    }

    @SuppressWarnings("unchecked")
    public User(Parcel p)
    {
        this.username = p.readString();
        this.email = p.readString();
        this.exp = p.readInt();
        this.level = p.readInt();
        this.profileImage = p.readString();
    }

    public User() { }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    @SuppressWarnings("rawtypes")
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.username);
        parcel.writeString(this.email);
        parcel.writeInt(this.exp);
        parcel.writeInt(this.level);
        parcel.writeString(this.profileImage);
    }
}