package com.example.svenu.svenuitendaalwhatsfordinner;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyFavouritesFragment extends ListFragment {

    private static String TAG = "userRecipes";
    private static String userid;

    private Activity context;
    private FirebaseDatabase database;
    private DatabaseReference userRef;
    private FirebaseAuth auth;
    private FirebaseUser user;

    private boolean isPublic;

    public MyFavouritesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle args = this.getArguments();

        database = FirebaseDatabase.getInstance();
        userRef = database.getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        context = (Activity) getContext();

        if (user == null) {
            logOut();
        }

        isPublic = args.getBoolean("isPublic");
        if (isPublic) {
            userid = "public";
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.favourites_all_name);
        }
        else {
            userid = user.getUid();
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.favourites_name);
        }

        fillInListView();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_favourites, container, false);
    }

    private void fillInListView() {
        //TODO: get information from database and fill in listview
        final ArrayList<Recipe> recipes = new ArrayList<>();

        DatabaseReference favouritesDatabase = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("recipes");
        favouritesDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Recipe recipe = snap.getValue(Recipe.class);
                    if (isPublic) {
                        if (recipe.isPublic) {
                            recipes.add(recipe);
                        }
                    }
                    else {
                        recipes.add(recipe);
                    }
                }
                RecipeAdapter recipeAdapter = new RecipeAdapter(context.getApplicationContext(), recipes);
                MyFavouritesFragment.this.setListAdapter(recipeAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //////////////////////////////////
    }

    private void logOut() {
        FirebaseAuth.getInstance().signOut();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_container, new LogInFragment(), "categories");
        ft.commit();
    }

}
