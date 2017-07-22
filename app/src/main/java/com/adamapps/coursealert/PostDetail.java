package com.adamapps.coursealert;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.adamapps.coursealert.model.Comment;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

public class PostDetail extends AppCompatActivity {

    DatabaseReference post;
    ImageView loadImage;
    TextView title, desc;
    Button send;
    EditText enter_comment;
    RecyclerView comment_list;
    DatabaseReference comment;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        Intent i = getIntent();
        final String post_key = i.getStringExtra("key");
        dialog = new ProgressDialog(this);
        dialog.setTitle("Posting Comment");
        dialog.setCanceledOnTouchOutside(false);

        loadImage = (ImageView) findViewById(R.id.post_detail_image);
        title = (TextView) findViewById(R.id.post_detail_title);
        desc = (TextView) findViewById(R.id.post_detail_description);
        send = (Button) findViewById(R.id.send_comment);
        enter_comment = (EditText) findViewById(R.id.enter_comment);
        comment_list = (RecyclerView) findViewById(R.id.commenting_list);

        post = FirebaseDatabase.getInstance().getReference().child("post").child(post_key);
        comment = FirebaseDatabase.getInstance().getReference().child("comment").child(post_key);

        post.child("image").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String image = dataSnapshot.getValue(String.class);
                Picasso.with(PostDetail.this).load(image).placeholder(R.drawable.ic_person).into(loadImage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        post.child("title").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String titles = dataSnapshot.getValue(String.class);
                title.setText(titles);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        post.child("desc").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String description = dataSnapshot.getValue(String.class);
                desc.setText(description);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        send.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                String text = enter_comment.getText().toString();
                if (TextUtils.isEmpty(text)) {
                    return;
                }
                dialog.show();
                Comment commenting = new Comment(FirebaseAuth.getInstance().getCurrentUser().getUid(), text);
                comment.push().setValue(commenting).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dialog.dismiss();
                        enter_comment.setText("");
                        Toast.makeText(PostDetail.this, "Success", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                        Toast.makeText(PostDetail.this, "Failed Check Network", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        FirebaseRecyclerAdapter<Comment, CommentHolder> adapter = new
                FirebaseRecyclerAdapter<Comment, CommentHolder>(Comment.class, R.layout.single_comment,
                        CommentHolder.class, FirebaseDatabase.getInstance().getReference().child("comment").child(post_key)) {
                    @Override
                    protected void populateViewHolder(CommentHolder viewHolder, Comment model, int position) {
                        viewHolder.setUid(model.getUid());
                        viewHolder.setUid(model.getText());
                        viewHolder.setImage(PostDetail.this, model.getUid());
                    }
                };
        comment_list.setAdapter(adapter);
        comment_list.setLayoutManager(new LinearLayoutManager(this));

    }

    public static class CommentHolder extends RecyclerView.ViewHolder {
        View mView;

        public CommentHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        void setUid(String uid) {
            final TextView name = (TextView) mView.findViewById(R.id.user_comment_name);

            DatabaseReference image_ref = FirebaseDatabase.getInstance().getReference().child("UserInfo");
            image_ref.child(uid).child("userName").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String pic = dataSnapshot.getValue(String.class);
                    name.setText(pic);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        void setText(String text) {
            final TextView texte = (TextView) mView.findViewById(R.id.user_comment_desc);
            texte.setText(text);
        }

        void setImage(final Context c, String uid) {
            DatabaseReference image_ref = FirebaseDatabase.getInstance().getReference().child("UserInfo");
            final CircularImageView compic = (CircularImageView) mView.findViewById(R.id.user_comment_image);
            image_ref.child(uid).child("userImage").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String pic = dataSnapshot.getValue(String.class);
                    Picasso.with(c).load(pic).placeholder(R.drawable.ic_person_smaller).into(compic);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

}
