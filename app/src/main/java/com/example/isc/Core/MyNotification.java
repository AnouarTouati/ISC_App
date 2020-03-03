package com.example.isc.Core;

public class MyNotification {

    private MyUser myUser;
    private String notificationTime, notificationText;

    public MyNotification(MyUser myUser, String notificationText, String notificationTime){
        this.myUser = myUser;
        this.notificationText = notificationText;
        this.notificationTime = notificationTime;
    }

    public MyUser getMyUser() {
        return myUser;
    }

    public void setMyUser(MyUser myUser) {
        this.myUser = myUser;
    }

    public String getNotificationTime() {
        return notificationTime;
    }

    public void setNotificationTime(String notificationTime) {
        this.notificationTime = notificationTime;
    }

    public String getNotificationText() {
        return notificationText;
    }

    public void setNotificationText(String notificationText) {
        this.notificationText = notificationText;
    }
}

