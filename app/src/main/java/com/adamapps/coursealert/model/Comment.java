package com.adamapps.coursealert.model;

/**
 * Created by user on 17-Jun-17.
 */

public class Comment {
    String uid;
    String text;

    public Comment() {

    }

    public Comment(String uid, String text) {
        this.uid = uid;
        this.text = text;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
