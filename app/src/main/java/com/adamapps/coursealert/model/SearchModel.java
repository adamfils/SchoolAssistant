package com.adamapps.coursealert.model;


public class SearchModel {
    private String userImage;
    private String userName;
    private String userDescription;
    private String userLevel;

    public SearchModel() {

    }

    public SearchModel(String userImage, String userName, String userDescription, String userLevel) {
        this.userImage = userImage;
        this.userName = userName;
        this.userDescription = userDescription;
        this.userLevel = userLevel;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserDescription() {
        return userDescription;
    }

    public void setUserDescription(String userDescription) {
        this.userDescription = userDescription;
    }

    public String getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(String userLevel) {
        this.userLevel = userLevel;
    }
}
