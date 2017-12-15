package com.example.svenu.svenuitendaalwhatsfordinner;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
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
 * Class where the user can add his own recipes
 */
public class AddRecipeFragment extends Fragment {

    private View rootView;
    private Activity context;
    private EditText editText;
    private Button submitIngredient;
    private Button submitRecipe;
    private LinearLayout ingredientLinearLayout;
    private LinearLayout labelLayout;
    private ArrayList<String> ingredients = new ArrayList<>();
    private TextView recipeName;
    private EditText myInstructions;

    private FirebaseUser user;
    private DatabaseReference mDatabase;

    private ArrayList<CheckBox> checkBoxes;

    public AddRecipeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // get rootview and set titlebar title
        rootView = inflater.inflate(R.layout.fragment_add_recipe, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.add_recipe_name);

        context = (Activity) getContext();

        editText = rootView.findViewById(R.id.ingredientName);
        submitIngredient = rootView.findViewById(R.id.buttonAddIngredient);
        submitRecipe = rootView.findViewById(R.id.buttonSubmitRecipe);
        ingredientLinearLayout = rootView.findViewById(R.id.ingredientLinearLayout);
        labelLayout = rootView.findViewById(R.id.labelLayout);
        recipeName = rootView.findViewById(R.id.my_recipe_name);
        myInstructions = rootView.findViewById(R.id.my_recipe_instructions);

        user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference("labels");

        submitIngredient.setOnClickListener(new GoSubmitIngredientClickListener());
        submitRecipe.setOnClickListener(new GoSubmitRecipeClickListener());

        // check if the user is signed in
        if (user == null) {
            logOut();
        }
        else {
            loadLabels();
        }

        // Inflate the layout for this fragment
        return rootView;
    }

    private void logOut() {
        FirebaseAuth.getInstance().signOut();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_container, new LogInFragment(), "categories");
        ft.commit();
    }

    private void loadLabels() {
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("inside", "OnDataChange");
                int idCount = 0;
                checkBoxes = new ArrayList<>();
                for (DataSnapshot snap: dataSnapshot.getChildren()) {
                    // calculate an id for the checkbox (werkt niet meer?)
                    int resourceId = context.getResources().getIdentifier("checkbox_" + idCount, "string", context.getPackageName());

                    // create checkbox
                    CheckBox checkBox = new CheckBox(context);

                    HealthLabel tagHealthlabel = snap.getValue(HealthLabel.class);
                    String checkBoxText = tagHealthlabel.getName().replace("-", " ");
                    checkBoxText = checkBoxText.substring(0, 1).toUpperCase() + checkBoxText.substring(1);

                    checkBox.setText(checkBoxText);
                    checkBox.setTag(tagHealthlabel);
                    checkBox.setId(resourceId);
                    checkBoxes.add(checkBox);
                    idCount += 1;
                }
                // show checkboxes
                updateCheckboxes();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updateCheckboxes() {
        Log.d("inside", "updateCheckboxes");
        int size = checkBoxes.size();
        for (int i = 0; i < size; i++) {
            labelLayout.addView(checkBoxes.get(i));
        }
    }

    private class GoSubmitIngredientClickListener implements View.OnClickListener {

        private int idCount = 0;

        @Override
        public void onClick(View view) {
            // function to add an ingredient
            String ingredient = editText.getText().toString();

            if (!ingredient.equals("")) {
                ingredients.add(ingredient);
                editText.setText("");

                // add ingredient to layout
                LayoutParams lparamsLinear = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                LinearLayout newLinearLayout = new LinearLayout(context);
                newLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
                newLinearLayout.setLayoutParams(lparamsLinear);

                // create resourceid (werkt niet meer?)
                int resourceId = context.getResources().getIdentifier("ingredient_" + idCount, "string", context.getPackageName());
                idCount += 1;
//                int resourceId = context.getResources().getIdentifier(ingredient, "string", context.getPackageName());
                Log.d("resourceId:", resourceId +"");
                newLinearLayout.setId(resourceId);
                newLinearLayout.setTag(ingredient);
                newLinearLayout.setGravity(View.FOCUS_RIGHT);

                LayoutParams lparams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                TextView newTextView = new TextView(context);
                newTextView.setLayoutParams(lparams);
                newTextView.setText(ingredient);
                newLinearLayout.addView(newTextView);

                Button delete = new Button(context);
                delete.setLayoutParams(lparams);
                delete.setTag(resourceId);
                delete.setText("Delete");
                delete.setOnClickListener(new GoDeleteClickListener());
                newLinearLayout.addView(delete);

                ingredientLinearLayout.addView(newLinearLayout);
            }
        }
    }

    private class GoDeleteClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            // delete ingredient
            Button button = (Button) view;
            int resourceId = (Integer) button.getTag();
            LinearLayout linearLayout = rootView.findViewById(resourceId);
            String ingredient = linearLayout.getTag().toString();
            ingredientLinearLayout.removeView(linearLayout);
            ingredients.remove(ingredient);
            Toast.makeText(context, ingredient + " deleted!", Toast.LENGTH_SHORT).show();
        }
    }

    private class GoSubmitRecipeClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            //define healthlabels
            ArrayList<HealthLabel> healthLabels = new ArrayList<>();
            for (int i = 0; i < labelLayout.getChildCount(); i++) {
                View v = labelLayout.getChildAt(i);
                if (v instanceof CheckBox) {
                    boolean isChecked = ((CheckBox) v).isChecked();
                    HealthLabel myHealthLabel = (HealthLabel) v.getTag();
                    myHealthLabel.setPreference(isChecked);
                    healthLabels.add(myHealthLabel);
                }
            }

            //define name of recipe
            String recipeNameText = recipeName.getText().toString();

            //define ingredients of recipe
            ArrayList<String> ingredients = new ArrayList<>();
            for (int i = 0; i < ingredientLinearLayout.getChildCount(); i++) {
                View v = ingredientLinearLayout.getChildAt(i);
                if (v instanceof LinearLayout) {
                    View newView = ((LinearLayout) v).getChildAt(0);
                    if (newView instanceof TextView) {
                        String ingredient = ((TextView) newView).getText().toString();
                        ingredients.add(ingredient);
                    }
                }
            }

            //define instructions
            String instructions = myInstructions.getText().toString();

            //define username
            String username = user.getEmail();

            //create recipe
            final Recipe myRecipe = new Recipe(username, recipeNameText, healthLabels, ingredients, instructions);

            //upload to database
            final DatabaseReference updateDatabase = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("recipes");
            updateDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    long size = dataSnapshot.getChildrenCount();
                    String recipeKey = "recipe_" + size;
                    updateDatabase.child(recipeKey).setValue(myRecipe);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}
