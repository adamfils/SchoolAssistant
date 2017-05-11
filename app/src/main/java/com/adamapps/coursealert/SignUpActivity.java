package com.adamapps.coursealert;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.ProviderQueryResult;
import com.ldoublem.loadingviewlib.view.LVEatBeans;

public class SignUpActivity extends AppCompatActivity {
    Spinner schoolSpinner,levelSpinner;
    TextInputEditText emailEdit,passEdit;
    RadioButton maleBtn,femaleBtn;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.showOverflowMenu();

        LVEatBeans load = (LVEatBeans) findViewById(R.id.loading);
        load.setVisibility(View.GONE);
        auth = FirebaseAuth.getInstance();

        emailEdit = (TextInputEditText) findViewById(R.id.signUpEmail);
        passEdit = (TextInputEditText) findViewById(R.id.signUpPass);

        schoolSpinner = (Spinner) findViewById(R.id.spinnerCourses);
        levelSpinner = (Spinner) findViewById(R.id.spinnerLevel);
        //String courses[]={"CSC 205","CSC 207","CSC 209","CSC 211","MAT 201"};
        String schools[] = {"UB","CUIB"};
        String level[]={"200","300","400","450"};
        ArrayAdapter<String> schoolAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,schools);
        ArrayAdapter<String> levelAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,level);
        schoolSpinner.setAdapter(schoolAdapter);
        levelSpinner.setAdapter(levelAdapter);
    }
    public void signMeUp(View view){
        emailEdit = (TextInputEditText) findViewById(R.id.signUpEmail);
        passEdit = (TextInputEditText) findViewById(R.id.signUpPass);
        final String email,pass;
        email = emailEdit.getText().toString().trim();
        pass = passEdit.getText().toString().trim();
        emailEdit.setEnabled(false);
        passEdit.setEnabled(false);

        final LVEatBeans load = (LVEatBeans) findViewById(R.id.loading);

        load.setVisibility(View.VISIBLE);
            load.setViewColor(Color.WHITE);
            load.setEyeColor(Color.parseColor("#006400"));
            load.startAnim(5000);

        auth.createUserWithEmailAndPassword(email,pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Toast.makeText(SignUpActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                    load.stopAnim();


                finish();
                startActivity(new Intent(SignUpActivity.this, EditProfileActivity.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                auth.fetchProvidersForEmail(email).addOnSuccessListener(new OnSuccessListener<ProviderQueryResult>() {
                    @Override
                    public void onSuccess(ProviderQueryResult providerQueryResult) {
                        Toast.makeText(SignUpActivity.this, "Email Already Exist", Toast.LENGTH_SHORT).show();

                        load.setVisibility(View.GONE);

                        emailEdit.setEnabled(true);
                        passEdit.setEnabled(true);
                    }
                });
            }
        });
    }
}
