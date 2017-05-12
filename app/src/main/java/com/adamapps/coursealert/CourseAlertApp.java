package com.adamapps.coursealert;

import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by user on 08-May-17.
 */

public class CourseAlertApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if(FirebaseAuth.getInstance()!=null)
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        /*Picasso.Builder builder = new Picasso.Builder(this);
        //builder.downloader(new OkHttpDownloader(this,Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(false);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);*/

    }

}
