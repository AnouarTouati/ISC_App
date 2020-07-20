package com.example.isc.Core;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.isc.Common;

public class MyUser implements Parcelable {
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


    protected MyUser(Parcel in) {
        profileImageBitmap = in.readParcelable(Bitmap.class.getClassLoader());
        fullName = in.readString();
        email = in.readString();
        password = in.readString();
        position = in.readInt();
        studentRegistrationNumber = in.readString();
        userID = in.readString();
    }

    public static final Creator<MyUser> CREATOR = new Creator<MyUser>() {
        @Override
        public MyUser createFromParcel(Parcel in) {
            return new MyUser(in);
        }

        @Override
        public MyUser[] newArray(int size) {
            return new MyUser[size];
        }
    };

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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(profileImageBitmap, flags);
        dest.writeString(fullName);
        dest.writeString(email);
        dest.writeString(password);
        dest.writeInt(position);
        dest.writeString(studentRegistrationNumber);
        dest.writeString(userID);
    }
}
