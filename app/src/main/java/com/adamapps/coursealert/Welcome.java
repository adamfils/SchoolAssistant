package com.adamapps.coursealert;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Welcome extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseAuth.AuthStateListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(user !=null ){
                    finish();
                    startActivity(new Intent(Welcome.this,HomeActivity.class));
                }
            }
        };

    }
    public void signIn(View view){
        startActivity(new Intent(this,SignInActivity.class));
    }
    public void signUp(View view){
        startActivity(new Intent(this,SignUpActivity.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(auth!=null)
        auth.addAuthStateListener(listener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(auth!=null)
        auth.removeAuthStateListener(listener);
    }
}
