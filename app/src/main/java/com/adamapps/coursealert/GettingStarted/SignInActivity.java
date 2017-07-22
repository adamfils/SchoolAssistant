package com.adamapps.coursealert.GettingStarted;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.adamapps.coursealert.HomeActivity;
import com.adamapps.coursealert.R;
import com.adamapps.coursealert.SelectFavourites;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.ldoublem.loadingviewlib.view.LVEatBeans;

public class SignInActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static final int RC_SIGN_INN = 901;
    TextInputEditText emailEdit, passEdit;
    FirebaseAuth auth;
    FirebaseAuth.AuthStateListener listener;
    GoogleApiClient mGoogleApiClient;
    SignInButton msignIn;

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

        msignIn = (SignInButton) findViewById(R.id.google);
        msignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SignInActivity.this, "Sign In", Toast.LENGTH_SHORT).show();
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_INN);
            }
        });

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

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_INN) {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
                progressDialog.dismiss();

            }
        }
    }

    public void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(SignInActivity.this, "Welcome Back " + auth.getCurrentUser().getDisplayName(), Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(new Intent(SignInActivity.this, SelectFavourites.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignInActivity.this, "Failed " + e, Toast.LENGTH_SHORT).show();

            }
        });
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


        /*if (Build.VERSION.SDK_INT >= 16) {*/
            load.setVisibility(View.VISIBLE);
            load.setViewColor(Color.WHITE);
            load.setEyeColor(Color.parseColor("#006400"));
            load.startAnim(5000);
            //load.animate();

        auth.signInWithEmailAndPassword(email, pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                finish();
                startActivity(new Intent(SignInActivity.this, SelectFavourites.class));
                Toast.makeText(SignInActivity.this, "Welcome Back " + auth.getCurrentUser().getDisplayName(), Toast.LENGTH_SHORT).show();

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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
