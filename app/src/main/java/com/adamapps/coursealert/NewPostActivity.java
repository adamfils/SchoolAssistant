package com.adamapps.coursealert;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.adamapps.coursealert.model.SendPost;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class NewPostActivity extends AppCompatActivity {
    private ImageView imageEdit;
    private EditText titleEdit,descEdit;
    FirebaseStorage storage;
    FirebaseAuth auth;
    FirebaseDatabase database;
    private DatabaseReference post_ref;
    private StorageReference pic_ref;
    String imageLinked = null;
    private MediaPlayer mp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.showOverflowMenu();


        mp = new MediaPlayer();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        post_ref = database.getReference();
        pic_ref = storage.getReference();

        titleEdit = (EditText) findViewById(R.id.post_title);
        descEdit = (EditText) findViewById(R.id.post_desc);
        imageEdit = (ImageView) findViewById(R.id.post_image);
        imageEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder build = new AlertDialog.Builder(NewPostActivity.this);
                View v = getLayoutInflater().inflate(R.layout.upload_selector, null);

                final ImageButton imagess, audio, video, doc;
                imagess = (ImageButton) v.findViewById(R.id.pick_image);
                imagess.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        YoYo.with(Techniques.Hinge).duration(500).playOn(imagess);
                        Intent picIntent = new Intent();
                        picIntent.setAction(Intent.ACTION_GET_CONTENT);
                        picIntent.setType("image/*");
                        startActivityForResult(picIntent, 100);
                        //dialog.dismiss();
                    }
                });

                audio = (ImageButton) v.findViewById(R.id.pick_audio);
                audio.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        YoYo.with(Techniques.Hinge).duration(500).playOn(audio);
                        Toast.makeText(NewPostActivity.this, "Coming Soon...", Toast.LENGTH_SHORT).show();
                        //dialog.dismiss();
                    }
                });

                video = (ImageButton) v.findViewById(R.id.pick_video);
                video.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        YoYo.with(Techniques.Hinge).duration(500).playOn(video);
                        Toast.makeText(NewPostActivity.this, "Coming Soon...", Toast.LENGTH_SHORT).show();
                        //dialog.dismiss();
                    }
                });

                doc = (ImageButton) v.findViewById(R.id.pick_document);
                doc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        YoYo.with(Techniques.Hinge).duration(500).playOn(doc);
                        Toast.makeText(NewPostActivity.this, "Coming Soon...", Toast.LENGTH_SHORT).show();
                        //dialog.dismiss();
                    }
                });
                build.setView(v);
                final AlertDialog dialog = build.create();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
    }


    public void publishPost(View view){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Button pubBtn = (Button) findViewById(R.id.publishButton);
        YoYo.with(Techniques.RubberBand).duration(500).playOn(pubBtn);
        assert user != null;
        String uid = user.getUid();
        String name = user.getDisplayName();
        String title = titleEdit.getText().toString().trim();
        String desc = descEdit.getText().toString().trim();

        if(TextUtils.isEmpty(title)||TextUtils.isEmpty(desc)){

            Toast.makeText(this, "Fill All Fields Before Proceeding", Toast.LENGTH_SHORT).show();
            return ;
        }

        if (imageLinked != null) {
            Toast.makeText(this, "Publishing....", Toast.LENGTH_SHORT).show();
            SendPost sendPost = new SendPost(imageLinked, title, desc, uid, name);
            post_ref.child("post").push().setValue(sendPost).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(NewPostActivity.this, "Posted", Toast.LENGTH_SHORT).show();
                    mp.reset();
                    mp = MediaPlayer.create(getApplicationContext(), R.raw.snd_post);
                    mp.start();
                    finish();
                    startActivity(new Intent(NewPostActivity.this,HomeActivity.class));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(NewPostActivity.this, "Could Not Post Check Network", Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(this, "Publishing....", Toast.LENGTH_SHORT).show();
            SendPost sendPost = new SendPost("NoImageHere", title, desc, uid, name);
            post_ref.child("post").push().setValue(sendPost).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(NewPostActivity.this, "Posted", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(new Intent(NewPostActivity.this,HomeActivity.class));
                    mp.reset();
                    mp = MediaPlayer.create(getApplicationContext(), R.raw.snd_post);
                    mp.start();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(NewPostActivity.this, "Could Not Post Check Network", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

   /* public Uri getUriFromPath(String filePath){
        long photoId;
        Uri photoUri = MediaStore.Images.Media.getContentUri("external");
        String[] projection = {MediaStore.Images.ImageColumns._ID};

        Cursor cursor = getContentResolver().query(photoUri,projection,
                MediaStore.Images.ImageColumns.DATA+"LIKE ?",new String[]{filePath},null);
        assert cursor != null;
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(projection[0]);
        photoId = cursor.getLong(columnIndex);
        cursor.close();
        return Uri.parse(photoUri.toString()+"/"+photoId);
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode==100 && resultCode == RESULT_OK){

            final Uri picUri = data.getData();

            CropImage.activity(picUri)
                    .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                    //.setAspectRatio(1,1)
                    .start(this);

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                final Uri pic = result.getUri();

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setCanceledOnTouchOutside(false);
                /*progressDialog.show();*/
                assert user != null && user.getDisplayName() != null;
                StorageReference ref = pic_ref.child("PostPics").child(user.getUid()).child(pic.getLastPathSegment());
                UploadTask task = ref.putFile(pic);
                task.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double Progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        progressDialog.setTitle("Uploaded " + Progress + "%");
                        progressDialog.show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(NewPostActivity.this, "Posted Successfully", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        Uri imageLink = taskSnapshot.getDownloadUrl();
                        imageEdit.setImageURI(pic);
                        assert imageLink != null;
                        imageLinked = imageLink.toString();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();

                        Toast.makeText(NewPostActivity.this, "Could Not Post Check Your Network And Try Again", Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception e = result.getError();
                for (int i = 0; i < 4; i++)
                    Toast.makeText(this, "Error Message " + e, Toast.LENGTH_LONG).show();
            }


        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mp.release();
    }
}
