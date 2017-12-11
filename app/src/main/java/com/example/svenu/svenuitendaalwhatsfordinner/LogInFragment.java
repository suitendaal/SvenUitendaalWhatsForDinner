package com.example.svenu.svenuitendaalwhatsfordinner;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
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
public class LogInFragment extends Fragment {

    private ArrayList<HealthLabel> healthLabels = new ArrayList<>();

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    private DatabaseReference database;
    private static final String TAG = "users";
    Activity theContext;
    View rootView;

    Button button1;
    Button button2;

    public LogInFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.app_name);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();

        rootView = inflater.inflate(R.layout.fragment_log_in, container, false);
        button1 = rootView.findViewById(R.id.buttonSignUp);
        button2 = rootView.findViewById(R.id.buttonLogIn);

        theContext = (Activity) getContext();

        setListener();
        button1.setOnClickListener(new GoButtonClickListener());
        button2.setOnClickListener(new GoButtonClickListener());

        // Inflate the layout for this fragment
        return rootView;
    }

    private void getHealthLabels(final String email) {

        DatabaseReference labelRef = database.child("labels");
        //You can use the single or the value.. depending if you want to keep track

        labelRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snap: dataSnapshot.getChildren()) {
                    String labelText = snap.getValue().toString();
                    Log.d("hi", labelText);
                    healthLabels.add(new HealthLabel(labelText, false));
                }
                storeUser(email);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(theContext, "loading labels falied", Toast.LENGTH_SHORT).show();
                storeUser(email);
            }
        });
    }

    private void storeUser(String email) {
        // Sign in success, update UI with the signed-in user's information
        user = auth.getCurrentUser();

        // Add user to database
        String userUid = user.getUid();
        UserData newUserData = new UserData(email, userUid, healthLabels);
        database.child(TAG).child(userUid).setValue(newUserData);
    }

    private void setListener() {
        Log.d(TAG, "setListener");
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Toast.makeText(theContext.getApplicationContext(), "Logged in",
                            Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onAuthStateChanged:signed_in" + user.getUid());
                    startFavouritesFragment();
                }
                else {
                    Toast.makeText(theContext.getApplicationContext(), "Log in failed",
                            Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authStateListener != null) {
            auth.removeAuthStateListener(authStateListener);
        }
    }

    public void createUser(final String email, String password) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(theContext, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            getHealthLabels(email);
//                            // Sign in success, update UI with the signed-in user's information
//                            user = auth.getCurrentUser();
//                            // Add user to database
//                            String userUid = user.getUid();
//                            UserData newUserData = new UserData(email, userUid, healthLabels);
//                            database.child(userUid).setValue(newUserData);
//
//                            Toast.makeText(theContext.getApplicationContext(), healthLabels.get(0).name,
//                                    Toast.LENGTH_SHORT).show();
                        }
                        else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(theContext.getApplicationContext(), "Cannot create user",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void logInUser(String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(theContext, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            user = auth.getCurrentUser();
                            startFavouritesFragment();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d(TAG, "signInWithEmail:failure");
                        }
                    }
                });
    }

    private void startFavouritesFragment() {
        database.child(user.getUid()).child("labels").setValue(healthLabels);
        MyFavouritesFragment myFavouritesFragment = new MyFavouritesFragment();
        Bundle args = new Bundle();
        args.putString("userUid", user.getUid());
        myFavouritesFragment.setArguments(args);
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, myFavouritesFragment)
                .addToBackStack(null)
                .commit();
    }

    private class GoButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            TextView emailTextView = rootView.findViewById(R.id.email);
            TextView passwordTextView = rootView.findViewById(R.id.password);

            String email = emailTextView.getText().toString();
            String password = passwordTextView.getText().toString();

            if (email.equals("") || password.equals("")) {
                Toast.makeText(theContext.getApplicationContext(), "WTF ben je aan het doen man?",
                        Toast.LENGTH_SHORT).show();
            }
            else {
                switch (view.getId()) {
                    case R.id.buttonLogIn:
                        logInUser(email, password);
                        break;
                    case R.id.buttonSignUp:
                        createUser(email, password);
                        break;
                }
            }
        }
    }
}
