package com.example.svenu.svenuitendaalwhatsfordinner;


import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserPreferenceFragment extends Fragment {

    View rootView;
    Activity context;

    private FirebaseUser user;
    private DatabaseReference mDatabase;
    private String TAG = "labels";

    private LinearLayout linearLayout;
    private ArrayList<CheckBox> checkBoxes;

    public UserPreferenceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_user_preference, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.user_name);

        context = (Activity) getContext();
        user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference(TAG);
        linearLayout = rootView.findViewById(R.id.LinearLayoutUserPreferences);

        setDataListener();

        // diet: balanced, high-protein, high-fiber, low-fat, low-carb, low-sodium
        // health: vegan, vegetarian, paleo, dairy-free, gluten-free, wheat-free, fat-free,
        // low-sugar, egg-free, peanut-free, tree-nut-free, soy-free, fish-free, shellfish-free
//        String[] labelTexts = new String[] {
//                "Balanced", "High protein", "High fiber", "Low fat", "Low carb", "Low sodium",
//                "Vegan", "Vegetarian", "Paleo", "Dairy free", "gluten-free", "wheat-free",
//                "fat-free", "low-sugar", "egg-free", "peanut-free", "tree-nut-free", "soy-free",
//                "fish-free", "shellfish-free"
//        };
//        String[] labelNames = new String[] {
//               "balanced", "high-protein", "high-fiber", "low-fat", "low-carb", "low-sodium",
//                "vegan", "vegetarian", "paleo", "dairy-free", "gluten-free", "wheat-free",
//                "fat-free", "low-sugar", "egg-free", "peanut-free", "tree-nut-free", "soy-free",
//                "fish-free", "shellfish-free"
//        };
//        int numberOfLabels = labelNames.length;
//
//        String[] labelTexts = new String[numberOfLabels];
//
//        CheckBox[] labels = new CheckBox[numberOfLabels];
//        for (int i = 0; i < numberOfLabels; i++) {
//            String labelText = labelNames[i].replace("-", " ");
//            labelText = labelText.substring(0, 1).toUpperCase() + labelText.substring(1);
//            labelTexts[i] = labelText;
//
//            int id = getResources().getIdentifier("label" + i, "id", context.getPackageName());
//            Log.d("id", "" + id);
//            CheckBox checkBox = rootView.findViewById(id);
//            checkBox.setText(labelText);
//            checkBox.setTag(labelNames[i]);
//            labels[i] = checkBox;
//        }

        //TODO

        // Inflate the layout for this fragment
        return rootView;
    }

    private void setDataListener() {
        //You can use the single or the value.. depending if you want to keep track
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                checkBoxes = new ArrayList<>();
                int idCount = 0;
                for (DataSnapshot snap: dataSnapshot.getChildren()) {
                    int resourceId = context.getResources().getIdentifier("checkbox_" + idCount, "string", context.getPackageName());
                    CheckBox checkBox = new CheckBox(context);
                    String checkBoxText = snap.getValue().toString();
                    String tagText = checkBoxText.replace("-", " ");
                    tagText = tagText.substring(0, 1).toUpperCase() + tagText.substring(1);
                    checkBox.setText(tagText);
                    checkBox.setTag(checkBoxText);
                    checkBox.setId(resourceId);
                    checkBoxes.add(checkBox);
                    idCount += 1;
                }
                updateCheckboxes();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updateCheckboxes() {
        int size = checkBoxes.size();
        for (int i = 0; i < size; i++) {
            linearLayout.addView(checkBoxes.get(i));
        }
    }
}
