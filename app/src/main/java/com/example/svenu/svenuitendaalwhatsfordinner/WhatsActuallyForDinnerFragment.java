package com.example.svenu.svenuitendaalwhatsfordinner;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/**
 * Does nothing yet
 */
public class WhatsActuallyForDinnerFragment extends Fragment {

    private View rootView;

    public WhatsActuallyForDinnerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_whats_actually_for_dinner, container, false);
        // set title bar title
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.app_name);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // ensure user is logged in
        if (user == null) {
            logOut();
        }
        else {

        }

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

}
