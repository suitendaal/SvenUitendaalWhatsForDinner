package com.example.svenu.svenuitendaalwhatsfordinner;


import android.app.Activity;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends ListFragment {

    private Activity context;

    private FirebaseUser user;
    private DatabaseReference mDatabase;

    private View rootView;
    private EditText editText;
    private Button button;

    private String userUid;
    private ArrayList<Recipe> recipes;
    private ArrayList<HealthLabel> healthLabels = new ArrayList<>();

    private String appId = "5ee6a4b0";
    private String appKey = "1617274bb22e85bbb72196ce4668e46a";

    private RequestQueue queue;

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_search, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.search_recipe_name);

        context = (Activity) getContext();

        // load views
        editText = rootView.findViewById(R.id.recipeSearchText);
        button = rootView.findViewById(R.id.buttonSearch);

        user = FirebaseAuth.getInstance().getCurrentUser();

        // ensure user is logged in
        if (user == null) {
            logOut();
        }
        else {
            userUid = user.getUid();
            mDatabase = FirebaseDatabase.getInstance().getReference(userUid);
            // load labels to checkbox
            getUserLabels();
        }

        // Inflate the layout for this fragment
        return rootView;
    }

    private void getUserLabels() {
        mDatabase.child("labels").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    // add healthlabels to list
                    HealthLabel healthLabel = snap.getValue(HealthLabel.class);
                    if (healthLabel.getPreference()) {
                        healthLabels.add(healthLabel);
                    }
                }
                // when healthlabels are loaded, setonclicklistener for search
                button.setOnClickListener(new GoButtonClickListener());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private class GoButtonClickListener implements View.OnClickListener {
        // button to search recipe
        @Override
        public void onClick(View view) {
            String recipeToSearch = editText.getText().toString();
            if (!recipeToSearch.equals("")) {
                searchRecipe(recipeToSearch);
            }
        }
    }

    private void searchRecipe(String recipeToSearch) {
        // separate diet and health labels
        ArrayList<String> health = new ArrayList<>();
        ArrayList<String> diet = new ArrayList<>();

        int length = healthLabels.size();
        for (int i = 0; i < length; i++) {
            HealthLabel healthLabel = healthLabels.get(i);
            String label = healthLabel.getCategory();
            if (label.equals("diet")) {
                diet.add(healthLabel.getName());
            }
            else {
                health.add(healthLabel.getName());
            }
        }
        Random random = new Random();

        String url = "https://api.edamam.com/search?q=" + recipeToSearch + "&app_id=" + appId + "&app_key=" + appKey;

        // choose random healthlabel
        if (health.size() > 0) {
            int healthIndex = random.nextInt(health.size());
            String healthText = health.get(healthIndex);
            url += "&health=" + healthText;
        }

        // choose random dietlabel
        if (diet.size() > 0) {
            int dietIndex = random.nextInt(diet.size());
            String dietText = diet.get(dietIndex);
            url += "&diet=" + dietText;
        }

        // load results in listview
        loadResults(url);
    }

    private void loadResults(String url) {
        queue = Volley.newRequestQueue(context);

        // Request a string response from the provided URL.
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    recipes = new ArrayList<>();
                    JSONArray hits = response.getJSONArray("hits");
                    for (int i = 0; i < hits.length(); i++) {
                        // save every hit to a recipe
                        JSONObject aRecipe = hits.getJSONObject(i).getJSONObject("recipe");
                        String username = user.getEmail();
                        String name = aRecipe.getString("label");
                        String source = aRecipe.getString("source");
                        String urlRecipe = aRecipe.getString("url");
                        String image = aRecipe.getString("image");
                        float calories = Float.parseFloat(aRecipe.getString("calories"));
                        ArrayList<String> ingredients = new ArrayList<>();
                        JSONArray ingredientsJSON = aRecipe.getJSONArray("ingredients");
                        for (int j = 0; j < ingredientsJSON.length(); j++) {
                            String ingredient = ingredientsJSON.getJSONObject(j).getString("text");
                            ingredients.add(ingredient);
                        }
                        ArrayList<HealthLabel> recipeHealthLabels = healthLabels;
                        JSONArray dietJSON = aRecipe.getJSONArray("dietLabels");
                        for (int j = 0; j < dietJSON.length(); j++) {
                            String recipeDietLabel = dietJSON.getString(j);
                            for (int k = 0; k < recipeHealthLabels.size(); k++) {
                                HealthLabel thisHealthLabel = recipeHealthLabels.get(k);
                                if (recipeDietLabel.equals(thisHealthLabel.getName())) {
                                    thisHealthLabel.setPreference(true);
                                }
                            }
                        }
                        JSONArray healthJSON = aRecipe.getJSONArray("healthLabels");
                        for (int j = 0; j < healthJSON.length(); j++) {
                            String recipeHealthLabel = healthJSON.getString(j);
                            for (int k = 0; k < recipeHealthLabels.size(); k++) {
                                HealthLabel thisHealthLabel = recipeHealthLabels.get(k);
                                if (recipeHealthLabel.equals(thisHealthLabel.getName())) {
                                    thisHealthLabel.setPreference(true);
                                }
                            }
                        }
                        Recipe recipe = new Recipe(username, name, source, urlRecipe, image, healthLabels, ingredients, calories);
                        recipes.add(recipe);
                    }
                    // show recipes in list
                    setAdapterToList();
                }
                catch (JSONException exception) {
                    Toast.makeText(context, "That didn't work!", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show();
            }
        });

        // Add the request to the RequestQueue.
        queue.add(jsObjRequest);
    }

    private void setAdapterToList() {
        // load listview
        RecipeAdapter recipeAdapter = new RecipeAdapter(context.getApplicationContext(), recipes);
        setListAdapter(recipeAdapter);
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

        // show recipe
        Recipe recipe = (Recipe) v.getTag();
        startRecipeFragment(recipe);
    }

    private void startRecipeFragment(final Recipe recipe) {
        //TODO: make new dialogfragment
        Toast.makeText(context, recipe.name, Toast.LENGTH_SHORT).show();

        // add recipe to user's favourites
        final DatabaseReference updateDatabase = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("recipes");
        updateDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long size = dataSnapshot.getChildrenCount();
                // recipe key
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
