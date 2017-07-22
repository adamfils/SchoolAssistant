package com.adamapps.coursealert.model;

/**
 * Created by user on 07-May-17.
 */

public class SendPost {
    public String image;
    public String title;
    public String desc;
    public String uid;
    public String name;

    public SendPost(String image, String title, String desc, String uid, String name) {
        this.image = image;
        this.title = title;
        this.desc = desc;
        this.uid = uid;
        this.name = name;
    }
}
