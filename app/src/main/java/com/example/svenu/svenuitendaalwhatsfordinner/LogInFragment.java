package com.example.svenu.svenuitendaalwhatsfordinner;


import android.app.Activity;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
 * Fragment to log in
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
        // get all healthlabel and store them with the new user
        DatabaseReference labelRef = database.child("labels");
        //You can use the single or the value.. depending if you want to keep track

        labelRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snap: dataSnapshot.getChildren()) {
                    HealthLabel healthLabel = snap.getValue(HealthLabel.class);
                    healthLabels.add(healthLabel);
                }
                storeUser(email);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
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
        // listener to check if the user is logged in, then directs it to the my favourites fragment
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
                    Log.d(TAG, "onAuthStateChanged:LogIn Failed");
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
        // create user
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(theContext, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            getHealthLabels(email);
                        }
                        else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(theContext.getApplicationContext(), "User already exists",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void logInUser(String email, String password) {
        // log in user
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
        FragmentManager fm = getFragmentManager();
        if (fm != null) {
            fm.popBackStack();
        }
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_container, myFavouritesFragment);
        ft.addToBackStack(null);
        ft.commit();
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

    public boolean onBackPressed() {
        getActivity().finish();
        return true;
    }
}
