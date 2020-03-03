package com.example.isc.Core;

import android.graphics.Bitmap;

public class MyUser {
    private Bitmap profileImageBitmap;
    private String fullName, position, email, password;
    private long studentCardNumber;
    private final String userID;//final since id should not change

    public MyUser(String userID,Bitmap profileImageBitmap, String fullName, String position){
        this.userID=userID;
        this.profileImageBitmap =profileImageBitmap;
        this.fullName = fullName;
        this.position = position;
    }

    public Bitmap getProfileImageBitmap() {
        return profileImageBitmap;
    }

    public void setProfileImageBitmap(Bitmap profileImageBitmap) {
        this.profileImageBitmap = profileImageBitmap;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getUserID(){return userID;}
}
