package com.adamapps.coursealert;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.adamapps.coursealert.model.SinglePostModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

public class CurrentUserProfile extends AppCompatActivity {

    RecyclerView singlePostRecycler;
    CircularImageView displayImage;
    FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    FirebaseUser user;
    DatabaseReference stat_ref;
    DatabaseReference databaseReference;
    Query mQuery;
    private boolean mProcessLike = false;
    DatabaseReference like_ref;
    DatabaseReference comment_ref;
    ImageButton deleteButton;
    DatabaseReference follow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_user_profile);

        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.showOverflowMenu();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = firebaseDatabase.getReference();
        stat_ref = firebaseDatabase.getReference();
        follow = firebaseDatabase.getReference();

        follow = firebaseDatabase.getReference().child("Followers");
        follow.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long countFollowers = dataSnapshot.child(user.getUid()).getChildrenCount();
                TextView followers = (TextView) findViewById(R.id.numberOfUserFollowers);
                followers.setText(String.valueOf(countFollowers));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        displayImage = (CircularImageView) findViewById(R.id.displayImage);

        if (user.getPhotoUrl() != null) {
            String pic = user.getPhotoUrl().toString();
            Picasso.with(CurrentUserProfile.this).load(pic).into(displayImage);
        }
        singlePostRecycler = (RecyclerView) findViewById(R.id.singleRecycler);
        stat_ref.keepSynced(true);
        String currentUserName = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference post_ref = FirebaseDatabase.getInstance().getReference().child("post");
        mQuery = post_ref.orderByChild("uid").equalTo(currentUserName);
        mQuery.keepSynced(true);


        FirebaseRecyclerAdapter<SinglePostModel, singleUserHolder> adapter = new
                FirebaseRecyclerAdapter<SinglePostModel, singleUserHolder>(SinglePostModel.class, R.layout.single_post_layout_unique
                        , singleUserHolder.class, mQuery) {
                    @Override
                    protected void populateViewHolder(final singleUserHolder viewHolder, SinglePostModel model, int position) {
                        final String post_key = getRef(position).getKey();

                        viewHolder.setImage(CurrentUserProfile.this, model.getImage());
                        viewHolder.setTitle(model.getTitle());
                        viewHolder.setDesc(model.getDesc());
                        viewHolder.setUid(model.getName());
                        viewHolder.setLikeBtn(post_key);
                        viewHolder.setUsersLike(post_key);
                        viewHolder.delete_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                viewHolder.deletePost(CurrentUserProfile.this, post_key);
                            }
                        });
                        viewHolder.like_btn.setOnLikeListener(new OnLikeListener() {
                            @Override
                            public void liked(final LikeButton likeButton) {
                                mProcessLike = true;

                                like_ref = databaseReference.child("Likes");
                                like_ref.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (mProcessLike) {

                                            like_ref.child(post_key).child(firebaseAuth.getCurrentUser().getUid())
                                                    .setValue(firebaseAuth.getCurrentUser().getDisplayName())
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Toast.makeText(CurrentUserProfile.this, "Liked", Toast.LENGTH_SHORT).show();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(CurrentUserProfile.this, "Could Not Like", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                            likeButton.setAnimationScaleFactor(3);
                                            mProcessLike = false;

                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }

                            @Override
                            public void unLiked(LikeButton likeButton) {
                                like_ref = databaseReference.child("Likes");
                                like_ref.child(post_key).child(firebaseAuth.getCurrentUser().getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(CurrentUserProfile.this, "DisLiked", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(CurrentUserProfile.this, "Could Not Dislike \n" + e, Toast.LENGTH_SHORT).show();
                                    }
                                });
                                likeButton.setAnimationScaleFactor(3);
                                mProcessLike = false;

                            }
                        });

                        viewHolder.comment_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent i = new Intent(CurrentUserProfile.this, PostDetail.class);
                                i.putExtra("key", post_key);
                                startActivity(i);
                            }
                        });
                    }
                };
        singlePostRecycler.setLayoutManager(new LinearLayoutManager(this));
        singlePostRecycler.setAdapter(adapter);
    }


    public static class singleUserHolder extends RecyclerView.ViewHolder {
        View mView;
        LikeButton like_btn;
        ImageButton comment_btn, delete_btn;
        TextView usersLike;
        DatabaseReference like_ref;
        FirebaseAuth auth;
        Boolean Remove = false;
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference commentReference = FirebaseDatabase.getInstance().getReference();
        Context con;

        public singleUserHolder(View itemView) {
            super(itemView);
            mView = itemView;

            like_btn = (LikeButton) mView.findViewById(R.id.like_button);
            comment_btn = (ImageButton) mView.findViewById(R.id.comment_btn);
            delete_btn = (ImageButton) mView.findViewById(R.id.deleteButton);

            usersLike = (TextView) mView.findViewById(R.id.usersWhoLike);
            like_ref = FirebaseDatabase.getInstance().getReference().child("Likes");
            auth = FirebaseAuth.getInstance();
            like_ref.keepSynced(true);
        }

        public void deletePost(Context c, final String post_key) {
            final NiftyDialogBuilder dialog = NiftyDialogBuilder.getInstance(c);
            dialog.withTitle("Delete")
                    .withDialogColor("#ff8a80")
                    .withTitleColor("#ffffff")
                    .withMessageColor("#ffffff")
                    .withIcon(R.drawable.ic_delete)
                    .withDuration(700)
                    .withEffect(Effectstype.Newspager)
                    .withButton1Text("OK")
                    .withButton2Text("Cancel")
                    .isCancelableOnTouchOutside(false)
                    .setButton1Click(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            DatabaseReference del = FirebaseDatabase.getInstance().getReference().child("post");
                            del.child(post_key).removeValue();
                            dialog.withIcon(R.drawable.ic_done);
                            dialog.cancel();

                            like_ref.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.child(post_key).hasChild(auth.getCurrentUser().getUid())) {
                                        like_ref.child(post_key).removeValue();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    })
                    .setButton2Click(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.cancel();
                        }
                    })
                    .withMessage("Are You Sure You Want To Delete?").show();
        }


        public void setUsersLike(String post_key) {


            like_ref.child(post_key).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    long userNumber = dataSnapshot.getChildrenCount();
                    usersLike.setText(String.valueOf(userNumber));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


        public void setLikeBtn(final String post_key) {
            like_ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(post_key).hasChild(auth.getCurrentUser().getUid())) {
                        //like_btn.setImageResource(R.drawable.ic_like_blue);
                        like_btn.setLiked(true);
                        like_btn.setAnimationScaleFactor(3);
                    } else {
                        //like_btn.setImageResource(R.drawable.ic_like_grey);
                        like_btn.setLiked(false);
                        like_btn.setAnimationScaleFactor(3);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        void setImage(Context context, String image) {
            ImageView postImage = (ImageView) mView.findViewById(R.id.singlePostImage);
            Picasso.with(context).load(image).
                    placeholder(R.drawable.ic_image).error(R.drawable.ic_image_coloured).into(postImage);
        }

        public void setTitle(String title) {
            TextView titleNew = (TextView) mView.findViewById(R.id.singlePostTitle);
            titleNew.setText(title);
        }

        void setDesc(String desc) {
            TextView description = (TextView) mView.findViewById(R.id.singlePostDescription);
            description.setText(desc);
        }

        void setUid(String uid) {
            TextView userId = (TextView) mView.findViewById(R.id.singleUserId);
            userId.setText(uid);
        }

        void setName(String name) {
            TextView userId = (TextView) mView.findViewById(R.id.singleUserId);
            userId.setText(name);
        }
    }
    public void followMe(View view) {
        startActivity(new Intent(this, EditProfileActivity.class));
    }

}
