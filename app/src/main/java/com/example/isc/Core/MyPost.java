package com.example.isc.Core;

import android.graphics.Bitmap;

public class MyPost {

    private String postedText, myPostLevel, myPostTagColleague, myPostEvents;
    private Bitmap postedImageBitmap;
    private MyUser myUser;
    private String postID;
   private int indexOfPostForThisUser;//cause we might have multiple post by a single user

    public MyPost(String postID,MyUser myUser,int indexOfPostForThisUser, String postedText, Bitmap postedImageBitmap,
                  String myPostLevel, String myPostTagColleague, String myPostEvents){
       this.postID=postID;
       this.postedText = postedText;
        this.postedImageBitmap = postedImageBitmap;
        this.myUser = myUser;
        this.myPostLevel = myPostLevel;
        this.myPostTagColleague = myPostTagColleague;
        this.myPostEvents = myPostEvents;
        this.indexOfPostForThisUser=indexOfPostForThisUser;
    }

    public String getMyPostEvents() {
        return myPostEvents;
    }

    public void setMyPostEvents(String myPostEvents) {
        this.myPostEvents = myPostEvents;
    }

    public String getMyPostLevel() {
        return myPostLevel;
    }

    public void setMyPostLevel(String myPostLevel) {
        this.myPostLevel = myPostLevel;
    }

    public String getMyPostTagColleague() {
        return myPostTagColleague;
    }

    public void setMyPostTagColleague(String myPostTagColleague) {
        this.myPostTagColleague = myPostTagColleague;
    }

    public String getPostedText() {
        return postedText;
    }

    public void setPostedText(String postedText) {
        this.postedText = postedText;
    }

    public MyUser getMyUser() {
        return myUser;
    }
    public String getPostID(){return postID;}

    public void setMyUser(MyUser myUser) {
        this.myUser = myUser;
    }

    public Bitmap getPostedImageBitmap() {
        return postedImageBitmap;
    }

    public void setPostedImage(Bitmap postedImageBitmap) {
        this.postedImageBitmap = postedImageBitmap;
    }
    public int getIndexOfPostForThisUser(){
        return indexOfPostForThisUser;
    }
}
