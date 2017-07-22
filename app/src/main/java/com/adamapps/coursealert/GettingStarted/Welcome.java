package com.adamapps.coursealert.GettingStarted;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.adamapps.coursealert.HomeActivity;
import com.adamapps.coursealert.R;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Welcome extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseAuth.AuthStateListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        auth = FirebaseAuth.getInstance();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(user !=null ){
                    startActivity(new Intent(Welcome.this,HomeActivity.class));
                    finish();
                }
            }
        };

    }
    public void signIn(View view){
        Button signIn = (Button) findViewById(R.id.signInWelcome);
        YoYo.with(Techniques.RubberBand).duration(2000).playOn(signIn);
        startActivity(new Intent(this,SignInActivity.class));
    }
    public void signUp(View view){
        Button signIn = (Button) findViewById(R.id.signUpWelcome);
        YoYo.with(Techniques.RubberBand).duration(2000).playOn(signIn);
        startActivity(new Intent(this,SignUpActivity.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(listener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        auth.removeAuthStateListener(listener);
    }
}
