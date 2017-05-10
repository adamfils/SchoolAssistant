package com.adamapps.coursealert;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ldoublem.loadingviewlib.view.LVEatBeans;

public class SignInActivity extends AppCompatActivity {
    TextInputEditText emailEdit, passEdit;
    FirebaseAuth auth;
    FirebaseAuth.AuthStateListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.showOverflowMenu();

        LVEatBeans load = (LVEatBeans) findViewById(R.id.loading);
        load.setVisibility(View.GONE);

        auth = FirebaseAuth.getInstance();

        emailEdit = (TextInputEditText) findViewById(R.id.signInEmail);
        passEdit = (TextInputEditText) findViewById(R.id.signInPass);

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(user !=null ){
                    finish();
                    startActivity(new Intent(SignInActivity.this,HomeActivity.class));
                }
            }
        };
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

    public void signMeIn(View view) {
        final String email, pass;
        email = emailEdit.getText().toString().trim();
        pass = passEdit.getText().toString().trim();
        emailEdit.setEnabled(false);
        passEdit.setEnabled(false);
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Email Empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(pass)) {
            Toast.makeText(this, "Password Empty", Toast.LENGTH_SHORT).show();
            return;
        }
        final LVEatBeans load = (LVEatBeans) findViewById(R.id.loading);
        final ProgressDialog progressDialog = new ProgressDialog(this);

        if (Build.VERSION.SDK_INT >= 15) {
            load.setVisibility(View.VISIBLE);
            load.setViewColor(Color.WHITE);
            load.setEyeColor(Color.parseColor("#006400"));
            load.startAnim(5000);
            //load.animate();
        } else {
            progressDialog.setTitle("Signing In");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);
        }
        auth.signInWithEmailAndPassword(email, pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                progressDialog.dismiss();
                finish();
                startActivity(new Intent(SignInActivity.this, HomeActivity.class));

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                load.setVisibility(View.GONE);
                emailEdit.setEnabled(true);
                passEdit.setEnabled(true);
                Toast.makeText(SignInActivity.this, "Error "+e, Toast.LENGTH_SHORT).show();

            }
        });
    }
}
