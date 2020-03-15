package com.example.isc.Core;

import android.graphics.Bitmap;

import com.example.isc.Common;

public class MyUser  {
    private Bitmap profileImageBitmap;
    private String fullName, email, password;
    private int position;
    private String studentRegistrationNumber;
    private final String userID;//final since id should not change


    public MyUser(String userID,Bitmap profileImageBitmap, String fullName, int position,String email,String studentRegistrationNumber){
        this.userID=userID;
        this.profileImageBitmap =profileImageBitmap;
        this.fullName = fullName;
        this.position = position;
        this.email=email;
        this.studentRegistrationNumber=studentRegistrationNumber;
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

    public int getPosition() {
        return position;
    }
   public String getPositionAsString(){return Common.position[position];}
    public void setPosition(int position) {
        this.position = position;
    }

    public String getUserID(){return userID;}
   public String getEmail(){return email;}
   public String getStudentRegistrationNumber(){return studentRegistrationNumber;}


}
