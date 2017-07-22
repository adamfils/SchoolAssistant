package com.adamapps.coursealert;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.adamapps.coursealert.GettingStarted.Welcome;
import com.adamapps.coursealert.model.SinglePostModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
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
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;

public class HomeActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    DatabaseReference reference;
    RecyclerView recyclerView;
    FirebaseAuth auth;
    FirebaseUser user;
    GoogleApiClient mGoogleApiClient;
    private boolean mProcessLike = false;
    DatabaseReference like_ref;
    DatabaseReference comment_ref;
    WaveSwipeRefreshLayout waveSwipeRefreshLayout;
    ImageButton deleteButton;
    private Query mQuery;
    MaterialSearchView search_view;
    FloatingActionButton post;
    FloatingActionMenu fab_menu;
    DatabaseReference postRef;
    private MediaPlayer mp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_activty);


        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.showOverflowMenu();


        mp = new MediaPlayer();
        post = (FloatingActionButton) findViewById(R.id.new_post);
        fab_menu = (FloatingActionMenu) findViewById(R.id.fab_menu);
        postRef = FirebaseDatabase.getInstance().getReference().child("post");


        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();


        search_view = (MaterialSearchView) findViewById(R.id.search_view);
        search_view.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {
                firebaseRecycler();
            }
        });
        search_view.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText != null && !newText.isEmpty()) {
                    mQuery = postRef.orderByChild("name").startAt(newText);
                    FirebaseRecyclerAdapter<SinglePostModel, PostViewHolder> adapter =
                            new FirebaseRecyclerAdapter<SinglePostModel, PostViewHolder>
                                    (SinglePostModel.class, R.layout.single_post_layout, PostViewHolder.class, mQuery) {
                                @Override
                                protected void populateViewHolder(PostViewHolder viewHolder, SinglePostModel model, int position) {

                                    final String post_key = getRef(position).getKey();

                                    viewHolder.setImage(HomeActivity.this, model.getImage());
                                    viewHolder.setTitle(model.getTitle());
                                    viewHolder.setDesc(model.getDesc());
                                    viewHolder.setUid(model.getName());
                                    viewHolder.setLikeBtn(post_key);
                                    viewHolder.setUsersLike(post_key);
                                    viewHolder.like_btn.setOnLikeListener(new OnLikeListener() {
                                        @Override
                                        public void liked(final LikeButton likeButton) {
                                            mProcessLike = true;

                                            like_ref = reference.child("Likes");
                                            like_ref.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    if (mProcessLike) {

                                                        like_ref.child(post_key).child(auth.getCurrentUser().getUid())
                                                                .setValue(auth.getCurrentUser().getDisplayName())
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        Toast.makeText(HomeActivity.this, "Liked", Toast.LENGTH_SHORT).show();
                                                                        mp.reset();
                                                                        mp = MediaPlayer.create(getApplicationContext(), R.raw.snd_like_story);
                                                                        mp.start();
                                                                    }
                                                                })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Toast.makeText(HomeActivity.this, "Could Not Like", Toast.LENGTH_SHORT).show();
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
                                            like_ref = reference.child("Likes");
                                            like_ref.child(post_key).child(auth.getCurrentUser().getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(HomeActivity.this, "DisLiked", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(HomeActivity.this, "Could Not Dislike \n" + e, Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                            likeButton.setAnimationScaleFactor(3);
                                            mProcessLike = false;

                                        }
                                    });

                                }

                            };
                    recyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
                    recyclerView.setAdapter(adapter);
                } else {
                    firebaseRecycler();
                }
                return true;
            }
        });

        reference = FirebaseDatabase.getInstance().getReference();
        postRef = reference.child("post");
        recyclerView = (RecyclerView) findViewById(R.id.post_preview);
        postRef.keepSynced(true);

        houseKeeping();

        like_ref.keepSynced(true);

        firebaseRecycler();
    }


    private void firebaseRecycler() {
        FirebaseRecyclerAdapter<SinglePostModel, PostViewHolder> adapter =
                new FirebaseRecyclerAdapter<SinglePostModel, PostViewHolder>
                        (SinglePostModel.class, R.layout.single_post_layout, PostViewHolder.class, postRef) {
                    @Override
                    protected void populateViewHolder(PostViewHolder viewHolder, SinglePostModel model, int position) {

                        final String post_key = getRef(position).getKey();

                        viewHolder.setImage(HomeActivity.this, model.getImage());
                        viewHolder.setTitle(model.getTitle());
                        viewHolder.setDesc(model.getDesc());
                        viewHolder.setUid(model.getName());
                        viewHolder.setLikeBtn(post_key);
                        viewHolder.setUsersLike(post_key);
                        viewHolder.like_btn.setOnLikeListener(new OnLikeListener() {
                            @Override
                            public void liked(final LikeButton likeButton) {
                                mProcessLike = true;

                                like_ref = reference.child("Likes");
                                like_ref.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (mProcessLike) {

                                            like_ref.child(post_key).child(auth.getCurrentUser().getUid())
                                                    .setValue(auth.getCurrentUser().getDisplayName())
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Toast.makeText(HomeActivity.this, "Liked", Toast.LENGTH_SHORT).show();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(HomeActivity.this, "Could Not Like", Toast.LENGTH_SHORT).show();
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
                                like_ref = reference.child("Likes");
                                like_ref.child(post_key).child(auth.getCurrentUser().getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(HomeActivity.this, "DisLiked", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(HomeActivity.this, "Could Not Dislike \n" + e, Toast.LENGTH_SHORT).show();
                                    }
                                });
                                likeButton.setAnimationScaleFactor(3);
                                mProcessLike = false;

                            }
                        });
                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                return;
                            }
                        });

                    }

                };
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void houseKeeping() {
        final DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        waveSwipeRefreshLayout = (WaveSwipeRefreshLayout) findViewById(R.id.refresh_layout);
        waveSwipeRefreshLayout.setWaveColor(Color.parseColor("#c85a54"));
        waveSwipeRefreshLayout.setOnRefreshListener(new WaveSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                connectedRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean connected = dataSnapshot.getValue(Boolean.class);
                        if (connected) {
                            Toast.makeText(HomeActivity.this, "You Are Connected", Toast.LENGTH_SHORT).show();
                            new Task().execute();
                        } else {
                            Toast.makeText(HomeActivity.this, "You Seem Not To Be Connected", Toast.LENGTH_SHORT).show();
                            new Task().execute();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        like_ref = reference.child("Likes");
        comment_ref = reference.child("Comments");

    }

    public void NewPost(View v) {
        fab_menu.close(true);
        startActivity(new Intent(this, NewPostActivity.class));
    }

    public void SearchUser(View v) {
        fab_menu.close(true);
        startActivity(new Intent(this, SearchUserActivity.class));
    }

    public void Profile(View v) {
        fab_menu.close(true);
        startActivity(new Intent(this, CurrentUserProfile.class));
    }

    public void Logout(View v) {
        fab_menu.close(true);
        FirebaseAuth.getInstance().signOut();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                Toast.makeText(HomeActivity.this, "Signed Out Hope To See You Soon", Toast.LENGTH_SHORT).show();
            }
        });
        finish();
        startActivity(new Intent(HomeActivity.this, Welcome.class));
    }

    private class Task extends AsyncTask<Void, Void, String[]> {

        @Override
        protected String[] doInBackground(Void... voids) {
            return new String[0];
        }

        @Override
        protected void onPostExecute(String[] strings) {
            waveSwipeRefreshLayout.setRefreshing(false);
            super.onPostExecute(strings);

        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        View mView;
        LikeButton like_btn;
        ImageButton comment_btn;
        TextView usersLike;
        DatabaseReference like_ref;
        FirebaseAuth auth;

        public PostViewHolder(View itemView) {
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

        public void setUsersLike(String post_key) {


            like_ref.child(post_key).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    long userNumber = dataSnapshot.getChildrenCount();
                /*for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    String users = dataSnapshot1.getValue(String.class);
                    StringBuilder buffer = new StringBuilder();
                    for (int i=0;i<userNumber;i++){
                        if(i>0){
                            buffer.append(",");
                        }
                        buffer.append(users);
                    }
                    usersLike.setText(buffer);
                }*/
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
                        like_btn.setAnimationScaleFactor(4);
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

        void setImage(final Context context, final String image) {
            final ImageView postImage = (ImageView) mView.findViewById(R.id.singlePostImage);
            Picasso.with(context).load(image).
                    placeholder(R.drawable.ic_image).error(R.drawable.ic_image_coloured).networkPolicy(NetworkPolicy.OFFLINE).into(postImage, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(context).load(image).
                            placeholder(R.drawable.ic_image).error(R.drawable.ic_image_coloured).into(postImage);
                }
            });
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        search_view.setMenuItem(item);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    Toast.makeText(HomeActivity.this, "Signed Out", Toast.LENGTH_SHORT).show();
                }
            });
            finish();
            startActivity(new Intent(HomeActivity.this, Welcome.class));
        }
        if (id == R.id.explore) {
            fab_menu.close(true);
            startActivity(new Intent(this, ExploreCategories.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (search_view.isSearchOpen()) {
            search_view.closeSearch();
        } else {
            super.onBackPressed();
        }
    }
}
