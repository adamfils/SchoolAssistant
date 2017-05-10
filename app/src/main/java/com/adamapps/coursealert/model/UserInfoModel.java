package com.adamapps.coursealert.model;

/**
 * Created by user on 04-May-17.
 */

public class UserInfoModel {
    String userName;
    String userDescription;
    String userGender;
    String userLevel;
    //String image;

    public UserInfoModel(String userName, String userDescription, String userGender, String userLevel) {
        this.userName = userName;
       // this.image = image;
        this.userDescription = userDescription;
        this.userGender = userGender;
        this.userLevel = userLevel;
    }
}
