package com.example.svenu.svenuitendaalwhatsfordinner;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * Class to show all users favourites
 */
public class AllFavouritesFragment extends ListFragment {

    private Activity context;
    private FirebaseUser user;

    public AllFavouritesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        user = FirebaseAuth.getInstance().getCurrentUser();
        context = (Activity) getContext();

        // check if user is logged in
        if (user == null) {
            logOut();
        }
        else {
            // set titlebar title
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.favourites_all_name);

            // show favourites
            fillInListView();
        }

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_favourites, container, false);
    }

    private void fillInListView() {
        final ArrayList<Recipe> recipes = new ArrayList<>();

        // load recipes from database
        DatabaseReference favouritesDatabase = FirebaseDatabase.getInstance().getReference("public_recipes");
        favouritesDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    // add recipe to list
                    Recipe recipe = snap.getValue(Recipe.class);
                    recipes.add(recipe);
                }
                // show list in listview
                RecipeAdapter recipeAdapter = new RecipeAdapter(context.getApplicationContext(), recipes);
                AllFavouritesFragment.this.setListAdapter(recipeAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void logOut() {
        // log out and return to log in page
        FirebaseAuth.getInstance().signOut();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_container, new LogInFragment(), "categories");
        ft.commit();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        // show whole recipe
        Recipe recipe = (Recipe) v.getTag();
        startRecipeFragment(recipe);
    }

    private void startRecipeFragment(final Recipe recipe) {
        //TODO: make new dialogfragment
        Toast.makeText(context, recipe.name, Toast.LENGTH_SHORT).show();

        //upload to database
        final DatabaseReference updateDatabase = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("recipes");
        updateDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long size = dataSnapshot.getChildrenCount();
                String recipeKey = "recipe_" + size;
                updateDatabase.child(recipeKey).setValue(recipe);
                Toast.makeText(context, recipe.name + " uploaded!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}