package com.adamapps.coursealert;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.adamapps.coursealert.model.UserInfoModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ldoublem.loadingviewlib.view.LVPlayBall;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class EditProfileActivity extends AppCompatActivity {
    private static final int PIC_CODE = 100;
    private EditText userName,userDescription,userGender,userLevel;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    StorageReference photoRef;
    String imageLink="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.showOverflowMenu();

        photoRef = FirebaseStorage.getInstance().getReference();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        userName = (EditText) findViewById(R.id.userDisplayName);
        userDescription = (EditText) findViewById(R.id.userDescription);
        userGender = (EditText) findViewById(R.id.userGender);
        userLevel = (EditText) findViewById(R.id.userLevel);

        check();

    }
    public void check(){
        final CircularImageView imageView = (CircularImageView) findViewById(R.id.userImage);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference photoRef = databaseReference.child("UserInfo")
                .child(user.getUid());
        photoRef.child("userImage").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String pic = dataSnapshot.getValue(String.class);
                        Picasso.with(EditProfileActivity.this).load(pic)
                                .networkPolicy(NetworkPolicy.OFFLINE).into(imageView, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Picasso.with(EditProfileActivity.this).load(pic).into(imageView);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        userName = (EditText) findViewById(R.id.userDisplayName);
        userDescription = (EditText) findViewById(R.id.userDescription);
        userGender = (EditText) findViewById(R.id.userGender);
        userLevel = (EditText) findViewById(R.id.userLevel);


        databaseReference.child("UserInfo").child(user.getUid())
                .child("userName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.getValue(String.class);
                if(name!=null)
                    userName.setText(name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        databaseReference.child("UserInfo").child(user.getUid())
                .child("userDescription").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String desc = dataSnapshot.getValue(String.class);
                if(desc!=null)
                    userDescription.setText(desc);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        databaseReference.child("UserInfo").child(user.getUid())
                .child("userGender").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String gender = dataSnapshot.getValue(String.class);
                if(gender!=null)
                    userGender.setText(gender);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        databaseReference.child("UserInfo").child(user.getUid())
                .child("userLevel").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String level = dataSnapshot.getValue(String.class);
                if(level!=null)
                    userLevel.setText(String.valueOf(level));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void pickPic(View view){
        Intent pic = new Intent();
        pic.setAction(Intent.ACTION_GET_CONTENT);
        pic.setType("image/*");
        startActivityForResult(pic,PIC_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PIC_CODE&&resultCode==RESULT_OK){
            final Uri picUri = data.getData();
            final CircularImageView imageView = (CircularImageView) findViewById(R.id.userImage);
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            StorageReference newPic  = photoRef.child("profile_pics").child(user.getUid()).child(picUri.getLastPathSegment());
            UploadTask uploadTask = newPic.putFile(picUri);
            final ProgressDialog progressDialog = new ProgressDialog(EditProfileActivity.this);
            progressDialog.setCanceledOnTouchOutside(false);
                    uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100* taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                    progressDialog.setTitle(progress+""+"% Uploaded");
                    progressDialog.show();
                    progressDialog.dismiss();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    imageView.setImageURI(picUri);
                    assert downloadUrl != null;
                    imageLink = downloadUrl.toString();
                    if(downloadUrl!=null)
                    databaseReference.child("UserInfo").child(user.getUid()).child("userImage")
                            .setValue(downloadUrl.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(EditProfileActivity.this, "Picture Updated", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditProfileActivity.this, "Ooops Try Again", Toast.LENGTH_SHORT).show();
                        }
                    });
                    Toast.makeText(EditProfileActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditProfileActivity.this, "Ooops Try Again", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_tick, menu);
            return true;
        }

    @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();
        if(id==R.id.saveInfo) {
            userName = (EditText) findViewById(R.id.userDisplayName);
            userDescription = (EditText) findViewById(R.id.userDescription);
            userGender = (EditText) findViewById(R.id.userGender);
            userLevel = (EditText) findViewById(R.id.userLevel);
            String name, desc, gender, level;
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            final LVPlayBall playball = (LVPlayBall) findViewById(R.id.loadingBall);
            final ProgressDialog progressDialog = new ProgressDialog(this);


            name = userName.getText().toString().trim();
            desc = userDescription.getText().toString().trim();
            gender = userGender.getText().toString().trim();
            level = userLevel.getText().toString().trim();
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(desc) || TextUtils.isEmpty(gender) || TextUtils.isEmpty(level)) {
                Toast.makeText(this, "Fill In All Blanks Before Proceeding", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (Build.VERSION.SDK_INT >= 16) {
                playball.setVisibility(View.VISIBLE);
                playball.setViewColor(R.color.colorPrimary);
                playball.startAnim(1000);
            } else {
                progressDialog.setTitle("Saving Info.....");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
            }


            /*if (TextUtils.isEmpty(imageLink)) {*/

                UserInfoModel userInfoModel = new UserInfoModel(name, desc, gender, level);
                assert user != null;
                databaseReference.child("UserInfo")
                        .child(user.getUid()).setValue(userInfoModel)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                playball.stopAnim();
                                playball.setVisibility(View.INVISIBLE);
                                progressDialog.dismiss();
                                startActivity(new Intent(EditProfileActivity.this, HomeActivity.class));
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        playball.stopAnim();
                        playball.setVisibility(View.INVISIBLE);
                        progressDialog.dismiss();
                    }
                });

            /*} else {
                UserInfoModel userInfoModel = new UserInfoModel(name, imageLink, desc, gender, level);
                databaseReference.child("UserInfo")
                        .child(user.getUid()).setValue(userInfoModel)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                playball.stopAnim();
                                playball.setVisibility(View.INVISIBLE);
                                progressDialog.dismiss();
                                startActivity(new Intent(EditProfileActivity.this, HomeActivity.class));
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        playball.stopAnim();
                        playball.setVisibility(View.INVISIBLE);
                        progressDialog.dismiss();
                    }
                });
            }*/
        }


            return super.onOptionsItemSelected(item);
        }

    @Override
    protected void onStart() {
        super.onStart();

    }
}
