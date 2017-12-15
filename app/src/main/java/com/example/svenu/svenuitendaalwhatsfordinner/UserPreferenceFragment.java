package com.example.svenu.svenuitendaalwhatsfordinner;


import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
    //private String TAG = ;

    private LinearLayout linearLayout;
    private Button button;
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

        // ensure user is logged in
        if (user == null) {
            logOut();
        }
        else {
            mDatabase = FirebaseDatabase.getInstance().getReference();
            linearLayout = rootView.findViewById(R.id.LinearLayoutUserPreferences);
            button = rootView.findViewById(R.id.buttonSubmitPreferences);

            // load checkboxes woth healthlabels
            setDataListener();
            button.setOnClickListener(new GoButtonClickListener());
        }

        // healthlabels:
        // diet: balanced, high-protein, high-fiber, low-fat, low-carb, low-sodium
        // health: vegan, vegetarian, paleo, dairy-free, gluten-free, wheat-free, fat-free,
        // low-sugar, egg-free, peanut-free, tree-nut-free, soy-free, fish-free, shellfish-free

        // Inflate the layout for this fragment
        return rootView;
    }

    private void logOut() {
        // log out and return to log in page
        FirebaseAuth.getInstance().signOut();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_container, new LogInFragment(), "categories");
        ft.commit();
    }

    private void setDataListener() {
        //You can use the single or the value.. depending if you want to keep track
        mDatabase.child("users").child(user.getUid()).child("labels").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("inside", "OnDataChange");
                checkBoxes = new ArrayList<>();
                int idCount = 0;
                for (DataSnapshot snap: dataSnapshot.getChildren()) {
                    // load healthlabel
                    int resourceId = context.getResources().getIdentifier("checkbox_" + idCount, "string", context.getPackageName());
                    CheckBox checkBox = new CheckBox(context);

                    HealthLabel healthLabel = snap.getValue(HealthLabel.class);
                    String tagText = healthLabel.getName();
                    String checkBoxText = tagText.replace("-", " ");
                    checkBoxText = checkBoxText.substring(0, 1).toUpperCase() + checkBoxText.substring(1);
                    boolean isChecked = healthLabel.getPreference();

                    // set checkboxvalues
                    checkBox.setText(checkBoxText);
                    checkBox.setTag(healthLabel);
                    checkBox.setChecked(isChecked);
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
        int size = checkBoxes.size();
        for (int i = 0; i < size; i++) {
            // show checkboxes
            linearLayout.addView(checkBoxes.get(i));
        }
    }

    private class GoButtonClickListener implements View.OnClickListener {
        // on button clicked, save preferences
        @Override
        public void onClick(View view) {
            int size = checkBoxes.size();
            ArrayList<HealthLabel> healthLabels = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                // save preferences
                CheckBox checkBox = checkBoxes.get(i);
                boolean isChecked = checkBox.isChecked();
                HealthLabel healthLabel = (HealthLabel) checkBox.getTag();
                healthLabel.setPreference(isChecked);
                healthLabels.add(healthLabel);
            }
            // update preferences to database
            updateDatabase(healthLabels);
            Toast.makeText(context, "Changes submitted", Toast.LENGTH_SHORT).show();
        }

        private void updateDatabase(ArrayList<HealthLabel> healthLabels) {
            // update preferences to database
            DatabaseReference userPreferences = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("labels");
            userPreferences.setValue(healthLabels);
        }
    }
}
