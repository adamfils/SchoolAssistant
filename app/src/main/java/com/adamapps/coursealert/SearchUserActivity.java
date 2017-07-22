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
import android.widget.TextView;
import android.widget.Toast;

import com.adamapps.coursealert.model.SearchModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

/**
 * Created by user on 27-May-17.
 */

public class SearchUserActivity extends AppCompatActivity {
    MaterialSearchView search_view;
    RecyclerView search_recycler;
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference user_ref;
    Query searchQuery;
    DatabaseReference follow;
    private static final boolean isFollowing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_users);
        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.showOverflowMenu();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        user_ref = database.getReference().child("UserInfo");
        searchQuery = user_ref;

        search_recycler = (RecyclerView) findViewById(R.id.search_result_view);


        search_view = (MaterialSearchView) findViewById(R.id.search_view);
        search_view.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {
                FirebaseRecyclerAdapter<SearchModel, SearchHolder> adapter =
                        new FirebaseRecyclerAdapter<SearchModel, SearchHolder>(SearchModel.class,
                                R.layout.user_search_result, SearchHolder.class, user_ref) {
                            @Override
                            protected void populateViewHolder(SearchHolder viewHolder, SearchModel model, int position) {
                                final String key = getRef(position).getKey();
                                viewHolder.setUserImage(SearchUserActivity.this, model.getUserImage());
                                viewHolder.setUserName(model.getUserName());
                                viewHolder.setUserDescription(model.getUserDescription());
                                viewHolder.setUserLevel(model.getUserLevel());
                                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent detail = new Intent(SearchUserActivity.this, SingleUserProfile.class);
                                        detail.putExtra("userID", key);
                                        detail.putExtra("userEmail", user.getEmail());
                                        if (key.equals(user.getUid())) {
                                            startActivity(new Intent(SearchUserActivity.this, CurrentUserProfile.class));
                                        } else {
                                            startActivity(detail);
                                        }
                                    }
                                });
                            }
                        };
                search_recycler.setLayoutManager(new LinearLayoutManager(SearchUserActivity.this));
                search_recycler.setAdapter(adapter);


            }
        });
        search_view.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                searchQuery = user_ref.orderByChild("userName").startAt(s);
                FirebaseRecyclerAdapter<SearchModel, SearchHolder> adapter =
                        new FirebaseRecyclerAdapter<SearchModel, SearchHolder>(SearchModel.class,
                                R.layout.user_search_result, SearchHolder.class, searchQuery) {
                            @Override
                            protected void populateViewHolder(SearchHolder viewHolder, SearchModel model, int position) {
                                final String key = getRef(position).getKey();
                                viewHolder.setUserImage(SearchUserActivity.this, model.getUserImage());
                                viewHolder.setUserName(model.getUserName());
                                viewHolder.setUserDescription(model.getUserDescription());
                                viewHolder.setUserLevel(model.getUserLevel());
                                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent detail = new Intent(SearchUserActivity.this, SingleUserProfile.class);
                                        detail.putExtra("userID", key);
                                        detail.putExtra("userEmail", user.getEmail());
                                        if (key.equals(user.getUid())) {
                                            startActivity(new Intent(SearchUserActivity.this, CurrentUserProfile.class));
                                        } else {
                                            startActivity(detail);
                                        }
                                    }
                                });
                            }
                        };
                search_recycler.setLayoutManager(new LinearLayoutManager(SearchUserActivity.this));
                search_recycler.setAdapter(adapter);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText != null && !newText.isEmpty()) {
                    searchQuery = user_ref.orderByChild("userName").startAt(newText);
                    FirebaseRecyclerAdapter<SearchModel, SearchHolder> adapter =
                            new FirebaseRecyclerAdapter<SearchModel, SearchHolder>(SearchModel.class,
                                    R.layout.user_search_result, SearchHolder.class, searchQuery) {
                                @Override
                                protected void populateViewHolder(SearchHolder viewHolder, SearchModel model, int position) {
                                    final String key = getRef(position).getKey();
                                    viewHolder.setUserImage(SearchUserActivity.this, model.getUserImage());
                                    viewHolder.setUserName(model.getUserName());
                                    viewHolder.setUserDescription(model.getUserDescription());
                                    viewHolder.setUserLevel(model.getUserLevel());
                                    viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent detail = new Intent(SearchUserActivity.this, SingleUserProfile.class);
                                            detail.putExtra("userID", key);
                                            detail.putExtra("userEmail", user.getEmail());
                                            if (key.equals(user.getUid())) {
                                                startActivity(new Intent(SearchUserActivity.this, CurrentUserProfile.class));
                                            } else {
                                                startActivity(detail);
                                            }
                                        }
                                    });
                                }
                            };
                    search_recycler.setLayoutManager(new LinearLayoutManager(SearchUserActivity.this));
                    search_recycler.setAdapter(adapter);

                } else {
                    Toast.makeText(SearchUserActivity.this, "Nothing Found", Toast.LENGTH_LONG).show();
                }
                return true;
            }
        });

        FirebaseRecyclerAdapter<SearchModel, SearchHolder> adapter =
                new FirebaseRecyclerAdapter<SearchModel, SearchHolder>(SearchModel.class,
                        R.layout.user_search_result, SearchHolder.class, user_ref) {
                    @Override
                    protected void populateViewHolder(SearchHolder viewHolder, SearchModel model, int position) {
                        final String key = getRef(position).getKey();
                        viewHolder.setUserImage(SearchUserActivity.this, model.getUserImage());
                        viewHolder.setUserName(model.getUserName());
                        viewHolder.setUserDescription(model.getUserDescription());
                        viewHolder.setUserLevel(model.getUserLevel());
                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent detail = new Intent(SearchUserActivity.this, SingleUserProfile.class);
                                detail.putExtra("userID", key);
                                detail.putExtra("userEmail", user.getEmail());
                                if (key.equals(user.getUid())) {
                                    startActivity(new Intent(SearchUserActivity.this, CurrentUserProfile.class));
                                } else {
                                    startActivity(detail);
                                }
                            }
                        });
                    }
                };
        search_recycler.setLayoutManager(new LinearLayoutManager(SearchUserActivity.this));
        search_recycler.setAdapter(adapter);
        searchQuery.keepSynced(true);
        user_ref.keepSynced(true);


    }


    public static class SearchHolder extends RecyclerView.ViewHolder {
        View mView;

        public SearchHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        void setUserImage(Context c, String userImage) {
            CircularImageView pic = (CircularImageView) mView.findViewById(R.id.user_search_image);
            Picasso.with(c).load(userImage).placeholder(R.drawable.ic_person_smaller).into(pic);
        }

        void setUserName(String userName) {
            TextView name = (TextView) mView.findViewById(R.id.user_search_name);
            name.setText(userName);
        }

        void setUserDescription(String userDescription) {
            TextView name = (TextView) mView.findViewById(R.id.user_search_desc);
            name.setText(userDescription);
        }

        void setUserLevel(String userLevel) {
            TextView name = (TextView) mView.findViewById(R.id.user_search_level);
            name.setText(userLevel);
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

        int id = item.getItemId();


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
