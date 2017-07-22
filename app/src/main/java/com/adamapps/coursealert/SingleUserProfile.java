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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.adamapps.coursealert.model.SinglePostModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
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
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class SingleUserProfile extends AppCompatActivity {
    RecyclerView singlePostRecycler;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    DatabaseReference user_ref;
    DatabaseReference post_ref, like_ref;
    Query postQuery;
    private boolean mProcessLike = false;
    DatabaseReference follow;
    private boolean isFollowing = false;
    String globalKey = null;
    DatabaseReference profile_details;
    DatabaseReference following;
    Query followingQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_user_profile);
        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.showOverflowMenu();
        //GEt INtent
        Intent i = getIntent();
        final String key = i.getStringExtra("userID");
        final String getEmail = i.getStringExtra("userEmail");
        //Toast.makeText(this, "Key is "+key, Toast.LENGTH_SHORT).show();
        globalKey = key;
        final Button followBtn = (Button) findViewById(R.id.followBtn);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        profile_details = firebaseDatabase.getReference().child("UserInfo").child(key);
        profile_details.child("userName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.getValue(String.class);
                if (name != null) {
                    TextView names = (TextView) findViewById(R.id.single_user_name);
                    names.setText(name);
                } else {
                    Toast.makeText(SingleUserProfile.this, "No UserName", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        profile_details.child("userImage").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String name = dataSnapshot.getValue(String.class);
                if (name != null) {
                    final ImageView img = (ImageView) findViewById(R.id.displayImage);
                    Picasso.with(SingleUserProfile.this).
                            load(name).networkPolicy(NetworkPolicy.OFFLINE).into(img, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(SingleUserProfile.this).load(name).into(img);
                        }
                    });
                } else {
                    Toast.makeText(SingleUserProfile.this, "No UserName", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        user_ref = firebaseDatabase.getReference().child("UserInfo");
        post_ref = firebaseDatabase.getReference().child("post");
        like_ref = firebaseDatabase.getReference();
        singlePostRecycler = (RecyclerView) findViewById(R.id.singleRecycler);
        follow = firebaseDatabase.getReference().child("Followers");
        following = follow;

        //Set Like Button Blue is User Already Likes
        follow.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //String id = dataSnapshot.getValue(String.class);
                if (dataSnapshot.hasChild(key)) {
                    if (dataSnapshot.child(key).hasChild(user.getUid())) {
                        followBtn.setText("Following");
                        followBtn.setBackgroundColor(Color.parseColor("#3e82fc"));
                        followBtn.setTextColor(Color.WHITE);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        follow.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long countFollowers = dataSnapshot.child(key).getChildrenCount();
                TextView followers = (TextView) findViewById(R.id.numberOfUserFollowers);
                followers.setText(String.valueOf(countFollowers));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        DatabaseReference post_ref = FirebaseDatabase.getInstance().getReference().child("post");
        postQuery = post_ref.orderByChild("uid").equalTo(key);
        postQuery.keepSynced(true);

        /*followingQuery = following.orderByChild(key).equalTo(getEmail);
        followingQuery.keepSynced(true);*/


        FirebaseRecyclerAdapter<SinglePostModel, userProfileHolder> adapter = new
                FirebaseRecyclerAdapter<SinglePostModel, userProfileHolder>(SinglePostModel.class, R.layout.single_post_layout
                        , userProfileHolder.class, postQuery) {
                    @Override
                    protected void populateViewHolder(userProfileHolder viewHolder, SinglePostModel model, int position) {
                        final String post_key = getRef(position).getKey();

                        viewHolder.setImage(SingleUserProfile.this, model.getImage());
                        viewHolder.setTitle(model.getTitle());
                        viewHolder.setDesc(model.getDesc());
                        viewHolder.setUid(model.getName());
                        viewHolder.setLikeBtn(post_key);
                        viewHolder.setUsersLike(post_key);
                        viewHolder.like_btn.setOnLikeListener(new OnLikeListener() {
                            @Override
                            public void liked(final LikeButton likeButton) {
                                mProcessLike = true;


                                like_ref.child("Likes").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (mProcessLike) {

                                            like_ref.child(post_key).child(firebaseAuth.getCurrentUser().getUid())
                                                    .setValue(firebaseAuth.getCurrentUser().getDisplayName())
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Toast.makeText(SingleUserProfile.this, "Liked", Toast.LENGTH_SHORT).show();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(SingleUserProfile.this, "Could Not Like", Toast.LENGTH_SHORT).show();
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
                                like_ref.child("Likes").child(post_key).child(firebaseAuth.getCurrentUser().getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(SingleUserProfile.this, "DisLiked", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(SingleUserProfile.this, "Could Not Dislike \n" + e, Toast.LENGTH_SHORT).show();
                                    }
                                });
                                likeButton.setAnimationScaleFactor(3);
                                mProcessLike = false;

                            }
                        });


                    }
                };
        singlePostRecycler.setLayoutManager(new LinearLayoutManager(this));
        singlePostRecycler.setAdapter(adapter);
        follow.keepSynced(true);
    }

    public static class userProfileHolder extends RecyclerView.ViewHolder {
        View mView;
        LikeButton like_btn;
        ImageButton comment_btn;
        TextView usersLike;
        DatabaseReference like_ref;
        FirebaseAuth auth;

        public userProfileHolder(View itemView) {
            super(itemView);
            mView = itemView;

            like_btn = (LikeButton) mView.findViewById(R.id.like_button);
            comment_btn = (ImageButton) mView.findViewById(R.id.comment_btn);

            usersLike = (TextView) mView.findViewById(R.id.usersWhoLike);
            like_ref = FirebaseDatabase.getInstance().getReference().child("Likes");
            auth = FirebaseAuth.getInstance();
            like_ref.keepSynced(true);
        }

        public void setComment(String post_key) {

        }

        void setUsersLike(String post_key) {


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


    public void singleFollow(View view) {
        final Button followBtn = (Button) findViewById(R.id.followBtn);
        isFollowing = true;
        follow.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (isFollowing) {
                    //Check If User Has Already Followed
                    if (dataSnapshot.child(globalKey).hasChild(user.getUid())) {
                        follow.child(globalKey).child(user.getUid())
                                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                followBtn.setText("Follow");
                                followBtn.setTextColor(Color.BLACK);
                                followBtn.setBackgroundColor(Color.parseColor("#ffffff"));
                                Toast.makeText(SingleUserProfile.this, "Unfollowed", Toast.LENGTH_SHORT).show();
                            }
                        });
                        isFollowing = false;
                    }//Else Start Following
                    else {
                        follow.child(globalKey).child(user.getUid())
                                .setValue(user.getEmail()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                followBtn.setText("Following");
                                followBtn.setTextColor(Color.WHITE);
                                followBtn.setBackgroundColor(Color.parseColor("#3e82fc"));
                                Toast.makeText(SingleUserProfile.this, "Following", Toast.LENGTH_SHORT).show();
                            }
                        });

                        isFollowing = false;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

}
