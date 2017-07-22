package com.adamapps.coursealert.GettingStarted;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.adamapps.coursealert.R;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

public class SplashScreen extends AppCompatActivity {

    ImageView logo;
    TextView logoText, developer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        logo = (ImageView) findViewById(R.id.logo);
        logoText = (TextView) findViewById(R.id.logoText);
        developer = (TextView) findViewById(R.id.developer);

        YoYo.with(Techniques.Landing).duration(2000).playOn(logo);
        YoYo.with(Techniques.RollIn).duration(2000).playOn(logoText);
        YoYo.with(Techniques.Tada).duration(2000).repeat(3).playOn(developer);

        new Splash().execute();

    }

    public class Splash extends AsyncTask {

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            startActivity(new Intent(SplashScreen.this, Welcome.class));
            finish();
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                Thread.sleep(6000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return true;
        }
    }
}
