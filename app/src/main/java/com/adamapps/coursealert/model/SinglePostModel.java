package com.adamapps.coursealert.model;

/**
 * Created by user on 07-May-17.
 */

public class SinglePostModel {
    private String image;
    private String title;
    private String desc;
    private String uid;

    public SinglePostModel(){

    }

    public SinglePostModel(String image, String title, String desc, String uid) {
        this.image = image;
        this.title = title;
        this.desc = desc;
        this.uid = uid;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
