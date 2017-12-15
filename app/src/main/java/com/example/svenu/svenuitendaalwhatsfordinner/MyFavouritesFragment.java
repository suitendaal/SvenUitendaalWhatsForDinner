package com.example.svenu.svenuitendaalwhatsfordinner;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
 * Fragment which shows users favourites
 */
public class MyFavouritesFragment extends ListFragment {


    private Activity context;
    private FirebaseUser user;

    public MyFavouritesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        user = FirebaseAuth.getInstance().getCurrentUser();
        context = (Activity) getContext();

        // ensure user is logged in
        if (user == null) {
            logOut();
        }
        else {
            // set title bar title
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.favourites_name);
            // fill in listview
            fillInListView();
        }

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_favourites, container, false);
    }

    private void fillInListView() {
        // function which fills in listview with user's favourite recipes
        final ArrayList<Recipe> recipes = new ArrayList<>();

        DatabaseReference favouritesDatabase = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("recipes");
        favouritesDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    // add recipe to list
                    Recipe recipe = snap.getValue(Recipe.class);
                    recipes.add(recipe);
                }
                // load list in listview
                RecipeAdapter recipeAdapter = new RecipeAdapter(context.getApplicationContext(), recipes);
                MyFavouritesFragment.this.setListAdapter(recipeAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void logOut() {
        // log out and return to login page
        FirebaseAuth.getInstance().signOut();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_container, new LogInFragment(), "categories");
        ft.commit();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        // show recipe
        Recipe recipe = (Recipe) v.getTag();
        startRecipeFragment(recipe);
    }

    private void startRecipeFragment(final Recipe recipe) {
        //TODO: make new dialogfragment
        Toast.makeText(context, recipe.name, Toast.LENGTH_SHORT).show();

        // add to all favourites
        final DatabaseReference updateDatabase = FirebaseDatabase.getInstance().getReference("public_recipes");
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
