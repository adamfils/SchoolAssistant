package com.adamapps.coursealert.model;

/**
 * Created by user on 07-May-17.
 */

public class SendPost {
    String image;
    String title;
    String desc;
    String uid;

    public SendPost(String image, String title, String desc, String uid) {
        this.image = image;
        this.title = title;
        this.desc = desc;
        this.uid = uid;
    }
}
