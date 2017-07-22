package com.adamapps.coursealert;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
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
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class EditProfileActivity extends AppCompatActivity {
    private static final int PIC_CODE = 100;
    private EditText userName, userDescription, userLevel;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    StorageReference photoRef;
    String imageLink = null;
    RadioButton maleBtn, femaleBtn;
    RadioGroup buttonGroup;
    Spinner levelSpinner, factSpinner, majorSpinner;
    String gender = null;
    String levelSelected = null, facultySelected = null;
    public static final String selectLevel = "Select Level";

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
        maleBtn = (RadioButton) findViewById(R.id.male);
        femaleBtn = (RadioButton) findViewById(R.id.female);
        levelSpinner = (Spinner) findViewById(R.id.levelInfo);
        factSpinner = (Spinner) findViewById(R.id.facultyInfo);
        majorSpinner = (Spinner) findViewById(R.id.majorInfo);
        buttonGroup = (RadioGroup) findViewById(R.id.radioGroup);
        buttonGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.male:
                        gender = "male";
                        break;
                    case R.id.female:
                        gender = "female";
                        break;
                    default:
                        gender = "none";
                        break;
                }
            }
        });
        final String[] levels = {selectLevel, "200", "300", "400", "450", "500"};
        String[] faculties = getResources().getStringArray(R.array.faculties);
        final String[] majorDefault = getResources().getStringArray(R.array.fs);
        final ArrayAdapter<String> levelAdapter = new ArrayAdapter<String>(this
                , android.R.layout.simple_dropdown_item_1line, levels);
        levelSpinner.setAdapter(levelAdapter);
        //levelSpinner.setSelection(2);
        /*levelSelected = levelSpinner.getSelectedItem().toString();*/

        levelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        levelSelected = levelSpinner.getSelectedItem().toString();

                        break;
                    case 1:
                        levelSelected = levelSpinner.getSelectedItem().toString();
                        break;
                    case 2:
                        levelSelected = levelSpinner.getSelectedItem().toString();
                        break;
                    case 3:
                        levelSelected = levelSpinner.getSelectedItem().toString();
                        break;
                    case 4:
                        levelSelected = levelSpinner.getSelectedItem().toString();
                        break;
                    case 5:
                        levelSelected = levelSpinner.getSelectedItem().toString();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        check();

        ArrayAdapter<String> facultyAdapter = new ArrayAdapter<String>(this
                , android.R.layout.simple_dropdown_item_1line, faculties);
        factSpinner.setAdapter(facultyAdapter);

        final String[] asti = getResources().getStringArray(R.array.asti);
        final String[] cot = getResources().getStringArray(R.array.cot);
        final String[] favm = getResources().getStringArray(R.array.favm);
        final String[] fa = getResources().getStringArray(R.array.fa);
        final String[] fed = getResources().getStringArray(R.array.fed);
        final String[] fet = getResources().getStringArray(R.array.fet);
        final String[] fhs = getResources().getStringArray(R.array.fhs);
        final String[] fs = getResources().getStringArray(R.array.fs);
        final String[] fsms = getResources().getStringArray(R.array.fsms);
        final String[] htttc = getResources().getStringArray(R.array.htttc);
        final String[] sym = getResources().getStringArray(R.array.sym);

       /* factSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        facultySelected = factSpinner.getSelectedItem().toString();
                        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(EditProfileActivity.this
                                , android.R.layout.simple_dropdown_item_1line, asti);
                        //majorSpinner.setAdapter(adapter1);
                        break;
                    case 1:
                        facultySelected = factSpinner.getSelectedItem().toString();
                        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(EditProfileActivity.this
                                , android.R.layout.simple_dropdown_item_1line, cot);
                       // majorSpinner.setAdapter(adapter2);
                        break;
                    case 2:
                        facultySelected = factSpinner.getSelectedItem().toString();
                        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(EditProfileActivity.this
                                , android.R.layout.simple_dropdown_item_1line, favm);
                       // majorSpinner.setAdapter(adapter3);
                        break;
                    case 3:
                        facultySelected = factSpinner.getSelectedItem().toString();
                        ArrayAdapter<String> adapter4 = new ArrayAdapter<String>(EditProfileActivity.this
                                , android.R.layout.simple_dropdown_item_1line, fa);
                       // majorSpinner.setAdapter(adapter4);
                        break;
                    case 4:
                        facultySelected = factSpinner.getSelectedItem().toString();
                        ArrayAdapter<String> adapter5 = new ArrayAdapter<String>(EditProfileActivity.this
                                , android.R.layout.simple_dropdown_item_1line, fed);
                       // majorSpinner.setAdapter(adapter5);
                        break;
                    case 5:
                        facultySelected = factSpinner.getSelectedItem().toString();
                        ArrayAdapter<String> adapter6 = new ArrayAdapter<String>(EditProfileActivity.this
                                , android.R.layout.simple_dropdown_item_1line, fet);
                       // majorSpinner.setAdapter(adapter6);
                        break;
                    case 6:
                        facultySelected = factSpinner.getSelectedItem().toString();
                        ArrayAdapter<String> adapter7 = new ArrayAdapter<String>(EditProfileActivity.this
                                , android.R.layout.simple_dropdown_item_1line, fhs);
                       // majorSpinner.setAdapter(adapter7);
                        break;
                    case 7:
                        facultySelected = factSpinner.getSelectedItem().toString();
                        ArrayAdapter<String> adapter8 = new ArrayAdapter<String>(EditProfileActivity.this
                                , android.R.layout.simple_dropdown_item_1line, fs);
                       // majorSpinner.setAdapter(adapter8);
                        break;
                    case 8:
                        facultySelected = factSpinner.getSelectedItem().toString();
                        ArrayAdapter<String> adapter9 = new ArrayAdapter<String>(EditProfileActivity.this
                                , android.R.layout.simple_dropdown_item_1line, fsms);
                       // majorSpinner.setAdapter(adapter9);
                        break;
                    case 9:
                        facultySelected = factSpinner.getSelectedItem().toString();
                        ArrayAdapter<String> adapter10 = new ArrayAdapter<String>(EditProfileActivity.this
                                , android.R.layout.simple_dropdown_item_1line, htttc);
                       // majorSpinner.setAdapter(adapter10);
                        break;
                    case 10:
                        facultySelected = factSpinner.getSelectedItem().toString();
                        ArrayAdapter<String> adapter11 = new ArrayAdapter<String>(EditProfileActivity.this
                                , android.R.layout.simple_dropdown_item_1line, sym);
                       // majorSpinner.setAdapter(adapter11);
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });*/


    }

    public void check() {
        final CircularImageView imageView = (CircularImageView) findViewById(R.id.userImage);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        assert user != null;
        DatabaseReference photoRef = databaseReference.child("UserInfo")
                .child(user.getUid());
        photoRef.child("userImage").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String pic = dataSnapshot.getValue(String.class);
                if (pic != null)
                    Picasso.with(EditProfileActivity.this).load(pic)
                            .networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.ic_image)
                            .into(imageView, new Callback() {
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


        databaseReference.child("UserInfo").child(user.getUid())
                .child("userName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.getValue(String.class);
                if (name != null)
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
                if (desc != null)
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
                if (gender != null) {
                    if (gender.equals("male")) {
                        maleBtn.setChecked(true);
                    } else if (gender.equals("female")) {
                        femaleBtn.setChecked(true);
                    } else {
                        Toast.makeText(EditProfileActivity.this, "Please Set Gender", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(EditProfileActivity.this, "Gender Is Empty", Toast.LENGTH_SHORT).show();
                }
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
                if (level != null) {
                    if (level.equals("200")) {
                        levelSpinner.setSelection(1);
                    } else if (level.equals("300")) {
                        levelSpinner.setSelection(2);
                    } else if (level.equals("400")) {
                        levelSpinner.setSelection(3);
                    } else if (level.equals("450")) {
                        levelSpinner.setSelection(4);
                    } else if (level.equals("500")) {
                        levelSpinner.setSelection(5);
                    } else {
                        levelSpinner.setSelection(1);
                    }
                } else {
                    levelSpinner.setSelection(1);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        databaseReference.child("UserInfo").child(user.getUid())
                .child("userFaculty").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String level = dataSnapshot.getValue(String.class);
                if (level != null) {
                    if (level.equals("ASTI")) {
                        levelSpinner.setSelection(1);
                    } else if (level.equals("COT")) {
                        levelSpinner.setSelection(2);
                    } else if (level.equals("FAVM")) {
                        levelSpinner.setSelection(3);
                    } else if (level.equals("FA")) {
                        levelSpinner.setSelection(4);
                    } else if (level.equals("FED")) {
                        levelSpinner.setSelection(5);
                    } else if (level.equals("FET")) {
                        levelSpinner.setSelection(6);
                    } else if (level.equals("FHS")) {
                        levelSpinner.setSelection(7);
                    } else if (level.equals("FS")) {
                        levelSpinner.setSelection(8);
                    } else if (level.equals("FSMS")) {
                        levelSpinner.setSelection(9);
                    } else if (level.equals("HTTTC")) {
                        levelSpinner.setSelection(10);
                    } else if (level.equals("SYM")) {
                        levelSpinner.setSelection(11);
                    } else {
                        levelSpinner.setSelection(1);
                    }
                } else {
                    levelSpinner.setSelection(7);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        /*databaseReference.child("UserInfo").child(user.getUid())
                .child("userMajor").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String level = dataSnapshot.getValue(String.class);
                if (level != null) {
                    if (level.equals("200")) {
                        levelSpinner.setSelection(1);
                    } else if (level.equals("300")) {
                        levelSpinner.setSelection(2);
                    } else if (level.equals("400")) {
                        levelSpinner.setSelection(3);
                    } else if (level.equals("450")) {
                        levelSpinner.setSelection(4);
                    } else if (level.equals("500")) {
                        levelSpinner.setSelection(5);
                    } else {
                        levelSpinner.setSelection(1);
                    }
                }else {
                    levelSpinner.setSelection(1);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/
    }

    public void pickPic(View view) {
        Intent pic = new Intent();
        pic.setAction(Intent.ACTION_GET_CONTENT);
        pic.setType("image/*");
        startActivityForResult(pic, PIC_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PIC_CODE && resultCode == RESULT_OK) {
            final Uri picUri = data.getData();

            CropImage.activity(picUri).setCropShape(CropImageView.CropShape.OVAL)
                    .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                    .setAspectRatio(1, 1)
                    .start(this);

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                final Uri picUri = result.getUri();
                /*String compressedImage = SiliCompressor.with(this).compress(String.valueOf(picUri));
                Toast.makeText(this, ""+compressedImage, Toast.LENGTH_LONG).show();*/
                final CircularImageView imageView = (CircularImageView) findViewById(R.id.userImage);
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                assert user != null;
                StorageReference newPic = photoRef.child("profile_pics").child(user.getUid()).child(picUri.getLastPathSegment());
                UploadTask uploadTask = newPic.putFile(picUri);
                final ProgressDialog progressDialog = new ProgressDialog(EditProfileActivity.this);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                        .setPhotoUri(picUri).build();
                user.updateProfile(request).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(EditProfileActivity.this,
                                "Updated Official Display Pic", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditProfileActivity.this, "" + e, Toast.LENGTH_SHORT).show();
                    }
                });
                uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        progressDialog.setTitle(progress + "" + "% Uploaded");
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


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception e = result.getError();
            }
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
        if (id == R.id.saveInfo) {

            userName = (EditText) findViewById(R.id.userDisplayName);
            userDescription = (EditText) findViewById(R.id.userDescription);
            String name, desc, level;
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            final LVPlayBall playball = (LVPlayBall) findViewById(R.id.loadingBall);

            name = userName.getText().toString().trim();
            desc = userDescription.getText().toString().trim();
            //gender = userGender.getText().toString().trim();
            String compare = levelSpinner.getSelectedItem().toString();
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(desc) || compare.equals("Select Level")
                    || TextUtils.isEmpty(gender) || gender.equals("none") || facultySelected == null) {
                Toast.makeText(this, "Fill In All Fields Before Proceeding", Toast.LENGTH_SHORT).show();
                return false;
            }

            playball.setVisibility(View.VISIBLE);
            playball.setViewColor(R.color.colorPrimary);
            playball.startAnim(1000);

            //UserInfoModel userInfoModel = new UserInfoModel(name, desc, gender, level);
            assert user != null;
            DatabaseReference infoRef = databaseReference.child("UserInfo").child(user.getUid());
            UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name).build();
            user.updateProfile(request).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    finish();
                    Toast.makeText(EditProfileActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditProfileActivity.this, "Could Not Update", Toast.LENGTH_SHORT).show();
                    return;
                }
            });
            infoRef.child("userName").setValue(name);
            infoRef.child("userDescription").setValue(desc);
            infoRef.child("userGender").setValue(gender);
            infoRef.child("userFaculty").setValue(facultySelected);
            infoRef.child("userLevel").setValue(levelSelected).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    playball.stopAnim();
                    playball.setVisibility(View.INVISIBLE);
                    startActivity(new Intent(EditProfileActivity.this, HomeActivity.class));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    playball.stopAnim();
                    playball.setVisibility(View.INVISIBLE);
                }
            });

        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }
}
