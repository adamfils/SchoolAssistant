package com.adamapps.coursealert;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.adamapps.coursealert.model.CategoriesModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;


public class ExploreCategories extends AppCompatActivity {

    DatabaseReference reference;
    RecyclerView recyclerView;
    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference catalog_ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore_categories);

        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.showOverflowMenu();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        recyclerView = (RecyclerView) findViewById(R.id.categories_recycler);
        reference = FirebaseDatabase.getInstance().getReference();
        catalog_ref = reference.child("category");


        firebaseUI();

    }

    private void firebaseUI() {
        FirebaseRecyclerAdapter<CategoriesModel, CategoriesHolder> adapter
                = new FirebaseRecyclerAdapter<CategoriesModel, CategoriesHolder>(
                CategoriesModel.class, R.layout.single_category_card,
                CategoriesHolder.class, catalog_ref) {
            @Override
            protected void populateViewHolder(CategoriesHolder viewHolder, CategoriesModel model, int position) {
                final String key = getRef(position).getKey();
                viewHolder.setIcon(ExploreCategories.this, model.getIcon());
                viewHolder.setTitle(model.getTitle());
                /*viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(ExploreCategories.this,HomeActivity.class);
                        i.putExtra("my_key",key);
                        startActivity(i);
                    }
                });*/
            }
        };
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);
    }


    public static class CategoriesHolder extends RecyclerView.ViewHolder {
        View mView;

        public CategoriesHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        void setIcon(final Context c, final String icon) {
            final CircularImageView image = (CircularImageView) mView.findViewById(R.id.subject_icon);
            Picasso.with(c).load(icon).placeholder(R.drawable.ic_person_smaller).networkPolicy(NetworkPolicy.OFFLINE).into(image, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(c).load(icon).placeholder(R.drawable.ic_person_smaller).into(image);
                }
            });
        }

        void setTitle(String title) {
            TextView titleSub = (TextView) mView.findViewById(R.id.subject_title);
            titleSub.setText(title);
        }
    }

}
