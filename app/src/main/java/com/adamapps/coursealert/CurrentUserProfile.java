package com.adamapps.coursealert;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

public class CurrentUserProfile extends AppCompatActivity {

    RecyclerView singlePostRecycler;
    CircularImageView displayImage;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private DatabaseReference stat_ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_user_profile);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        displayImage = (CircularImageView) findViewById(R.id.displayImage);
        stat_ref = firebaseDatabase.getReference();
        if (user.getPhotoUrl() != null) {
            String pic = user.getPhotoUrl().toString();
            Picasso.with(CurrentUserProfile.this).load(pic).into(displayImage);
        } else {
            stat_ref.child(user.getUid()).child("userImage").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String pic = dataSnapshot.getValue(String.class);
                    Picasso.with(CurrentUserProfile.this).load(pic).into(displayImage);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        singlePostRecycler = (RecyclerView) findViewById(R.id.singleRecycler);
        stat_ref.keepSynced(true);

    }

    public void followMe(View view) {
        startActivity(new Intent(this, EditProfileActivity.class));
    }
}
