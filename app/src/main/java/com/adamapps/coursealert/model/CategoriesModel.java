package com.adamapps.coursealert.model;

/**
 * Created by user on 29-May-17.
 */

public class CategoriesModel {
    private String icon;
    private String title;

    public CategoriesModel() {
        //Default
    }

    public CategoriesModel(String icon, String title) {
        this.icon = icon;
        this.title = title;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
