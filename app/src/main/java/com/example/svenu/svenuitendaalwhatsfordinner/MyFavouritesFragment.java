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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

        boolean isPublic = args.getBoolean("isPublic");
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

        //////////////////////////////test
        Log.d("inside", "inside fillInListView");
        ArrayList<String> arrayList = new ArrayList<>();
        ArrayList<String> ingredients = new ArrayList<>();
        ingredients.add("saus");
        ingredients.add("macaroni");
        String instructions = "Kook de macaroni en gooi de saus erbij";
        Recipe myRecipe = new Recipe(user.getEmail(), "Macaroni", arrayList, ingredients, instructions);

        ArrayList<Recipe> recipes = new ArrayList<>();

        if (!userid.equals("public")) {
            recipes.add(myRecipe);
        }

        Log.d("inside", "creating adapter");
        RecipeAdapter recipeAdapter = new RecipeAdapter(context.getApplicationContext(), recipes);

        MyFavouritesFragment.this.setListAdapter(recipeAdapter);
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
