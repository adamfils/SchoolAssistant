package com.adamapps.coursealert;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.adamapps.coursealert.model.SinglePostModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class HomeActivity extends AppCompatActivity {
    DatabaseReference reference;
    DatabaseReference postRef;
    RecyclerView recyclerView;

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

        reference = FirebaseDatabase.getInstance().getReference();
        postRef = reference.child("post");
        recyclerView = (RecyclerView) findViewById(R.id.post_preview);
        postRef.keepSynced(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<SinglePostModel, PostViewHolder> adapter =
                new FirebaseRecyclerAdapter<SinglePostModel, PostViewHolder>
                        (SinglePostModel.class, R.layout.single_post_layout, PostViewHolder.class, postRef) {
                    @Override
                    protected void populateViewHolder(PostViewHolder viewHolder, SinglePostModel model, int position) {

                        final String post_key = getRef(position).getKey();

                        viewHolder.setImage(HomeActivity.this, model.getImage());
                        viewHolder.setTitle(model.getTitle());
                        viewHolder.setDesc(model.getDesc());
                        viewHolder.setUid(model.getUid());
                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Toast.makeText(HomeActivity.this, "My Key Is " + post_key, Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                };
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public PostViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

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

        public void setUid(String uid) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            assert user != null;
            String EMAIL = user.getEmail();
            TextView userId = (TextView) mView.findViewById(R.id.singleUserId);
            userId.setText(EMAIL);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.profile) {

            startActivity(new Intent(this, EditProfileActivity.class));

        }
        if (id == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            finish();
            startActivity(new Intent(HomeActivity.this, Welcome.class));
        }
        if (id == R.id.addPost) {
            startActivity(new Intent(this, NewPostActivity.class));
        }


        return super.onOptionsItemSelected(item);
    }
}
